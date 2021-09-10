package com.mobdeve.awitize.service

import android.app.Notification
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.gms.location.*
import com.mobdeve.awitize.enums.PlayerServiceEvents
import com.mobdeve.awitize.helpers.LocationHelper
import com.mobdeve.awitize.model.Music
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class PlayerService : LifecycleService() {

    inner class PlayerBinder : Binder(){
        fun getService() : PlayerService{
            return this@PlayerService
        }
    }

    private val TAG = "PlayerService"
    private var nowPlaying : MediaItem? = null
    private lateinit var player : SimpleExoPlayer
    private var queue : LinkedList<MediaItem> = LinkedList()
    private var history : LinkedList<MediaItem> = LinkedList()
    private var currentCountry : String? = null
    private var locationHelper : LocationHelper? = null

    //Notification
    private var notif : Notification? = null

    //BroadcastReceivers
    private val destroyReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            this@PlayerService.stopSelf()
        }
    }
    private val sessionDestroyReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            destroySession()
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
        return PlayerBinder()
    }

    fun queueSong(music: Music){
        val queueThread = Runnable {
            val metaData = MediaMetadata.Builder()
            val bannedBundle = Bundle()
            bannedBundle.putStringArrayList("banned", music.banned)
            metaData.setExtras(bannedBundle)
            metaData.setTitle(music.title)
            metaData.setArtist(music.artist)
            metaData.setArtworkUri(Uri.parse(music.albumCoverURL))
            val mediaItem = MediaItem.Builder().setUri(music.audioFileURL).setMediaMetadata(metaData.build()).build()
            queue.add(mediaItem)
            val i = Intent(PlayerServiceEvents.NEW_SONG.name)
            i.putExtra("message", "Queued " + music.artist + " - " + music.title)
            LocalBroadcastManager.getInstance(this@PlayerService).sendBroadcast(i)
        }
        Executors.newSingleThreadExecutor().execute(queueThread)
    }

    fun playImmediately(music: Music){
        val metaData = MediaMetadata.Builder()
        val bannedBundle = Bundle()
        bannedBundle.putStringArrayList("banned", music.banned)
        metaData.setExtras(bannedBundle)
        metaData.setTitle(music.title)
        metaData.setArtist(music.artist)
        metaData.setArtworkUri(Uri.parse(music.albumCoverURL))
        val mediaItem = MediaItem.Builder().setUri(music.audioFileURL).setMediaMetadata(metaData.build()).build()
        if(history.size > 5 && nowPlaying != null){
            history.pollFirst()
            history.add(nowPlaying!!)
        }
        val nextMusic = mediaItem
        val banned = (nextMusic.mediaMetadata.extras?.get("banned") as ArrayList<String>)
        if(banned.size > 0 && (currentCountry == null || currentCountry == "")){
            Toast.makeText(this, "Skipped ${nextMusic.mediaMetadata.artist} - ${nextMusic.mediaMetadata.title} because of region-lock (Location not available)", Toast.LENGTH_SHORT).show()
            playNextSong()
            return
        }
        if(banned.indexOf(currentCountry) > -1){
            Toast.makeText(this, "Skipped ${nextMusic.mediaMetadata.artist} - ${nextMusic.mediaMetadata.title} because the song is not available in your country.", Toast.LENGTH_SHORT).show()
            playNextSong()
            return
        }
        nowPlaying = nextMusic
        player.setMediaItem(nowPlaying!!)
        player.prepare()
        player.playWhenReady = true
    }

    private fun playNextSong() {
        if(queue.size > 0){
            if(history.size > 5)
                history.pollFirst()
            if(nowPlaying != null){
                history.add(nowPlaying!!)
            }
            val nextMusic = queue.pollFirst()
            val banned = (nextMusic.mediaMetadata.extras?.get("banned") as ArrayList<String>)
            if(banned.size > 0 && (currentCountry == null || currentCountry == "")){
                Toast.makeText(this, "Skipped ${nextMusic.mediaMetadata.artist} - ${nextMusic.mediaMetadata.title} because of region-lock (Location not available)", Toast.LENGTH_SHORT).show()
                playNextSong()
                return
            }
            if(banned.indexOf(currentCountry) > -1){
                Toast.makeText(this, "Skipped ${nextMusic.mediaMetadata.artist} - ${nextMusic.mediaMetadata.title} because the song is not available in your country.", Toast.LENGTH_SHORT).show()
                playNextSong()
                return
            }
            nowPlaying = nextMusic
            player.setMediaItem(nowPlaying!!)
            player.prepare()
            player.playWhenReady = true
        }
        else{
            Toast.makeText(this, "No Next Song in Queue", Toast.LENGTH_SHORT).show()
            if(!player.isPlaying && player.playbackState == ExoPlayer.STATE_ENDED){
                nowPlaying = null
                player.playWhenReady = false
                player.removeMediaItem(0)
                val i = Intent(PlayerServiceEvents.PLAYER_STATE_CHANGED.name)
                LocalBroadcastManager.getInstance(this@PlayerService).sendBroadcast(i)
            }
        }
    }

    private fun playPrevSong() {
        if(history.size > 0){
            queue.addFirst(nowPlaying)
            nowPlaying = history.pollLast()
            player.setMediaItem(nowPlaying!!)
            player.prepare()
            player.playWhenReady = true
        }
        else{
            Toast.makeText(this, "Last Remembered Song Reached", Toast.LENGTH_SHORT).show()
        }
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

    private fun destroySession() {
        player.release()
        initPlayer()
        queue.clear()
        history.clear()
        nowPlaying = null
    }

    private fun initPlayer() {
        val extractor = DefaultExtractorsFactory().setMp3ExtractorFlags(Mp3Extractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING)
        player = SimpleExoPlayer.Builder(this).setMediaSourceFactory(DefaultMediaSourceFactory(this, extractor)).build()
        player.addListener(object: Player.Listener{

            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d(TAG, "onPlaybackStateChanged: ${player.duration}")
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

        var playerNotificationManager = PlayerNotificationManager.Builder(this, 1, "Awitize")
        playerNotificationManager.setMediaDescriptionAdapter(createMediaDescriptionAdapter())
        playerNotificationManager.setNotificationListener(object: PlayerNotificationManager.NotificationListener{
            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                super.onNotificationCancelled(notificationId, dismissedByUser)
                if(dismissedByUser){
                    stopSelf()
                }
            }
        })
        playerNotificationManager.build().setPlayer(player)
    }

    private fun createMediaDescriptionAdapter(): PlayerNotificationManager.MediaDescriptionAdapter {
        return object: PlayerNotificationManager.MediaDescriptionAdapter{
            override fun getCurrentContentTitle(player: Player): CharSequence {
                return "Awitize"
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                return null
            }

            override fun getCurrentContentText(player: Player): CharSequence? {
                val title = if(nowPlaying == null) "No Song" else nowPlaying!!.mediaMetadata.title.toString()
                val artist = if(nowPlaying == null) "No Song" else nowPlaying!!.mediaMetadata.artist.toString()
                return "$artist - $title"
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                return null
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        initPlayer()
        locationHelper = LocationHelper(this)
        locationHelper?.currentCountry?.observe(this, androidx.lifecycle.Observer {
            currentCountry = it
        })
//        val builder = NotificationCompat.Builder(this@PlayerService, "Awitize")
//        builder.setContentTitle("Awitize")
//        builder.setContentText("Notification for Audio Playback")
//        builder.setSmallIcon(R.drawable.logo___awitize)
//        notif = builder.build()
//        startForeground(1, notif)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        LocalBroadcastManager.getInstance(this).registerReceiver(destroyReceiver, IntentFilter(PlayerServiceEvents.DESTROY.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(newSongReceiver, IntentFilter(PlayerServiceEvents.NEW_SONG.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(playPauseReceiver, IntentFilter(PlayerServiceEvents.PLAY_PAUSE.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(skipNextReceiver, IntentFilter(PlayerServiceEvents.SKIP_NEXT.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(skipPrevReceiver, IntentFilter(PlayerServiceEvents.SKIP_PREV.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(sessionDestroyReceiver, IntentFilter(PlayerServiceEvents.SESSION_DESTROY.name))
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locationHelper?.destroy()
        player.release()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(destroyReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newSongReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playPauseReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(skipNextReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(skipPrevReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sessionDestroyReceiver)
    }
}