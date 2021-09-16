package com.mobdeve.awitize.service

import android.app.Notification
import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.gms.location.*
import com.mobdeve.awitize.Awitize
import com.mobdeve.awitize.enums.PlayerServiceEvents
import com.mobdeve.awitize.helpers.LocationHelper
import com.mobdeve.awitize.model.Music
import com.mobdeve.awitize.R
import com.mobdeve.awitize.activity.LoginActivity
import com.mobdeve.awitize.activity.MainActivity
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class PlayerService : LifecycleService() {

    inner class PlayerBinder : Binder(){
        fun getService() : PlayerService{
            return this@PlayerService
        }
    }

    private var nowPlaying : MediaItem? = null
    private lateinit var player : SimpleExoPlayer
    private var queue : LinkedList<MediaItem> = LinkedList()
    private var history : LinkedList<MediaItem> = LinkedList()
    private var currentCountry : String? = null
    private var locationHelper : LocationHelper? = null
    private var bindCount : Long = 0L

    val currentQueue : LinkedList<MediaItem>
        get() = queue

    //Notification
    private lateinit var notif : Notification
    private lateinit var playerNotification : Notification
    private lateinit var notificationManager : NotificationManagerCompat

    //BroadcastReceivers
    private val destroyReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            this@PlayerService.stopSelf()
        }
    }
    private val sessionDestroyReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            destroySession()
            if(bindCount <= 0){
                stopForeground(true)
                stopSelf()
            }
        }
    }
    private val newSongReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra("message")
            Toast.makeText(this@PlayerService, message, Toast.LENGTH_SHORT).show()
            if(!player.isPlaying && player.playbackState != ExoPlayer.STATE_READY){
                playNextSong()
            }
        }
    }
    private val playPauseReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(nowPlaying != null){
                player.playWhenReady = !player.playWhenReady
                showNotification()
            }
        }
    }
    private val skipPrevReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            playPrevSong()
        }
    }
    private val skipNextReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            playNextSong()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        bindCount++
        return PlayerBinder()
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        bindCount++
    }

    override fun onUnbind(intent: Intent?): Boolean {
        bindCount--
        return super.onUnbind(intent)
    }

    fun queueSong(music: Music){
        val queueThread = Runnable {
            val mediaItem = generateMediaItem(music)
            queue.add(mediaItem)
            val i = Intent(PlayerServiceEvents.NEW_SONG.name)
            i.putExtra("message", "Queued " + music.artist + " - " + music.title)
            LocalBroadcastManager.getInstance(this@PlayerService).sendBroadcast(i)
        }
        Executors.newSingleThreadExecutor().execute(queueThread)
    }

    fun playImmediately(music: Music){
        val mediaItem = generateMediaItem(music)
        if(history.size > 5){
            history.pollFirst()
        }
        if(nowPlaying != null){
            history.add(nowPlaying!!)
        }
        playSong(mediaItem)
    }

    fun isPlaying(): Boolean{
        return player.playWhenReady
    }

    fun getNowPlaying(): MediaItem?{
        return nowPlaying
    }

    fun connectPlayerView(playerView: PlayerView) {
        playerView.player = player
    }

    private fun playNextSong() {
        if(queue.size > 0){
            if(history.size > 5){
                history.pollFirst()
            }
            if(nowPlaying != null){
                history.add(nowPlaying!!)
            }
            val nextMusic = queue.pollFirst()
            playSong(nextMusic)
        }
        else{
            Toast.makeText(this, "No Next Song in Queue", Toast.LENGTH_SHORT).show()
            if(!player.isPlaying && player.playbackState == ExoPlayer.STATE_ENDED){
                nowPlaying = null
                player.playWhenReady = false
                player.removeMediaItem(0)
                val i = Intent(PlayerServiceEvents.PLAYER_STATE_CHANGED.name)
                LocalBroadcastManager.getInstance(this@PlayerService).sendBroadcast(i)
                showIdleNotif()
                notificationManager.cancel(2)
                if(bindCount <= 0){
                    destroySession()
                    stopForeground(true)
                    stopSelf()
                }
            }
        }
    }

    private fun playPrevSong() {
        if(history.size > 0){
            queue.addFirst(nowPlaying)
            playSong(history.pollLast())
        }
        else{
            Toast.makeText(this, "Last Remembered Song Reached", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playSong(mediaItem: MediaItem){
        val banned = (mediaItem.mediaMetadata.extras?.get("banned") as ArrayList<String>)
        if(banned.size > 0 && (currentCountry == null || currentCountry == "")){
            Toast.makeText(this, "Skipped ${mediaItem.mediaMetadata.artist} - ${mediaItem.mediaMetadata.title} because of region-lock (Location not available)", Toast.LENGTH_SHORT).show()
            playNextSong()
            return
        }
        if(banned.indexOf(currentCountry) > -1){
            Toast.makeText(this, "Skipped ${mediaItem.mediaMetadata.artist} - ${mediaItem.mediaMetadata.title} because the song is not available in your country.", Toast.LENGTH_SHORT).show()
            playNextSong()
            return
        }
        nowPlaying = mediaItem
        player.setMediaItem(nowPlaying!!)
        player.prepare()
        player.playWhenReady = true
        showNotification()
    }

    private fun generateMediaItem(music: Music) : MediaItem{
        val metaData = MediaMetadata.Builder()
        val bannedBundle = Bundle()
        bannedBundle.putStringArrayList("banned", music.banned)
        metaData.setExtras(bannedBundle)
        metaData.setTitle(music.title)
        metaData.setArtist(music.artist)
        metaData.setArtworkUri(Uri.parse(music.albumCoverURL))
        return MediaItem.Builder().setUri(music.audioFileURL).setMediaMetadata(metaData.build()).build()
    }

    private fun destroySession() {
        player.release()
        initPlayer()
        queue.clear()
        history.clear()
        nowPlaying = null
        val i = Intent(PlayerServiceEvents.PLAYER_STATE_CHANGED.name)
        LocalBroadcastManager.getInstance(this@PlayerService).sendBroadcast(i)
        notificationManager.cancel(2)
    }

    private fun initPlayer() {
        val extractor = DefaultExtractorsFactory().setMp3ExtractorFlags(Mp3Extractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING)
        player = SimpleExoPlayer.Builder(this).setMediaSourceFactory(DefaultMediaSourceFactory(this, extractor)).build()
        player.addListener(object: Player.Listener{

            override fun onPlaybackStateChanged(playbackState: Int) {
                val i = Intent(PlayerServiceEvents.PLAYER_STATE_CHANGED.name)
                LocalBroadcastManager.getInstance(this@PlayerService).sendBroadcast(i)
                if(playbackState == ExoPlayer.STATE_ENDED){
                    playNextSong()
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                val i = Intent(PlayerServiceEvents.PLAYER_STATE_CHANGED.name)
                LocalBroadcastManager.getInstance(this@PlayerService).sendBroadcast(i)
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
                val i = Intent(PlayerServiceEvents.PLAYER_STATE_CHANGED.name)
                LocalBroadcastManager.getInstance(this@PlayerService).sendBroadcast(i)
            }

        })
    }

    private fun showNotification(){
        val skipPrevIntent = PendingIntent.getBroadcast(this, 0, Intent(PlayerServiceEvents.SKIP_PREV.name), 0)
        val skipNext = PendingIntent.getBroadcast(this, 0, Intent(PlayerServiceEvents.SKIP_NEXT.name), 0)
        val playPause = PendingIntent.getBroadcast(this, 0, Intent(PlayerServiceEvents.PLAY_PAUSE.name), 0)
        val sessionDestroy = PendingIntent.getBroadcast(this, 0, Intent(PlayerServiceEvents.SESSION_DESTROY.name), 0)
        val playPauseIcon = if(nowPlaying!=null && player.playWhenReady) R.drawable.exo_icon_pause else R.drawable.exo_icon_play
        val touchNotif = PendingIntent.getActivity(this, 0, Intent(this, LoginActivity::class.java), 0)
        val noti = NotificationCompat.Builder(this@PlayerService, Awitize.GENERAL_CHANNEL_ID)
        noti.setSmallIcon(R.drawable.logo___awitize)
        noti.setContentTitle(nowPlaying?.mediaMetadata?.title)
        noti.setContentIntent(touchNotif)
        noti.setContentText(nowPlaying?.mediaMetadata?.artist)
        noti.addAction(R.drawable.exo_icon_previous, "prev", skipPrevIntent)
        noti.addAction(playPauseIcon, "playpause", playPause)
        noti.addAction(R.drawable.exo_icon_next, "next", skipNext)
        noti.setDeleteIntent(sessionDestroy)
        noti.setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0,1,2))
        noti.priority = NotificationCompat.PRIORITY_HIGH
        playerNotification = noti.build()
        notificationManager.notify(2, playerNotification)
    }

    private fun showIdleNotif(){
        val builder = NotificationCompat.Builder(this@PlayerService, Awitize.GENERAL_CHANNEL_ID)
        builder.setContentTitle("Awitize ${currentCountry?:""}")
        builder.setContentText(if(currentCountry === null) "Location is not available, unable to stream songs that have region-lock." else "Location is available.")
        builder.setSmallIcon(R.drawable.logo___awitize)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        notif = builder.build()
        startForeground(1, notif)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
        initPlayer()
        locationHelper = LocationHelper.getObjectInstance()
        locationHelper?.currentCountry?.observe(this, {
            currentCountry = it
            if(!player.isPlaying){
                showIdleNotif()
            }
        })
        LocalBroadcastManager.getInstance(this).registerReceiver(destroyReceiver, IntentFilter(PlayerServiceEvents.DESTROY.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(newSongReceiver, IntentFilter(PlayerServiceEvents.NEW_SONG.name))
        registerReceiver(playPauseReceiver, IntentFilter(PlayerServiceEvents.PLAY_PAUSE.name))
        registerReceiver(skipNextReceiver, IntentFilter(PlayerServiceEvents.SKIP_NEXT.name))
        registerReceiver(skipPrevReceiver, IntentFilter(PlayerServiceEvents.SKIP_PREV.name))
        registerReceiver(sessionDestroyReceiver, IntentFilter(PlayerServiceEvents.SESSION_DESTROY.name))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        LocationHelper.destroy()
        player.release()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(destroyReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newSongReceiver)
        unregisterReceiver(playPauseReceiver)
        unregisterReceiver(skipNextReceiver)
        unregisterReceiver(skipPrevReceiver)
        unregisterReceiver(sessionDestroyReceiver)
    }
}