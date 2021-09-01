package com.mobdeve.awitize.service

import android.app.IntentService
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.analytics.PlaybackStatsListener
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.mobdeve.awitize.enums.PlayerServiceEvents
import com.mobdeve.awitize.model.Music
import java.net.URL
import java.util.*

class PlayerService : Service() {

    private val TAG = "PlayerService"

    inner class PlayerBinder : Binder(){
        public fun getService() : PlayerService?{
            return this@PlayerService
        }
    }

    private var nowPlaying : MediaItem? = null
    private lateinit var player : SimpleExoPlayer
    private var queue : LinkedList<MediaItem> = LinkedList()
    private var history : LinkedList<MediaItem> = LinkedList()

    //BroadcastReceivers
    private val destroyReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            stopSelf()
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
            player.playWhenReady = !player.playWhenReady
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

    public fun queueSong(music: Music){
        val queueThread = object: Thread(){
            override fun run() {
                Looper.prepare()
                val metaData = MediaMetadata.Builder()
                val albumURL = URL(music.albumCoverURL)
                metaData.setTitle(music.title)
                metaData.setArtist(music.artist)
                metaData.setArtworkUri(Uri.parse(music.albumCoverURL))
//                metaData.setArtworkData(albumURL.readBytes(), MediaMetadata.PICTURE_TYPE_FRONT_COVER)
                var mediaItem = MediaItem.Builder().setUri(music.audioFileURL).setMediaMetadata(metaData.build()).build()
                queue.add(mediaItem)
                var i = Intent(PlayerServiceEvents.NEW_SONG.name)
                i.putExtra("message", "Queued " + music.artist + " - " + music.title)
                LocalBroadcastManager.getInstance(this@PlayerService).sendBroadcast(i)
            }
        }
        queueThread.start()
    }

    private fun playNextSong() {
        if(queue.size > 0){
            if(history.size > 5)
                history.pollFirst()
            if(nowPlaying != null){
                history.add(nowPlaying!!)
            }
            nowPlaying = queue.pollFirst()
            player.setMediaItem(nowPlaying!!)
            player.prepare()
            player.playWhenReady = true
        }
        else{
            Toast.makeText(this, "No Next Song in Queue", Toast.LENGTH_SHORT).show()
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

    public fun isPlaying(): Boolean{
        return player.playWhenReady
    }

    public fun getNowPlaying(): MediaItem?{
        return nowPlaying
    }

    public fun getCurrentDuration(): Long{
        return player.currentPosition
    }

    public fun getMaxDuration(): Long{
        return player.contentDuration
    }

    override fun onBind(intent: Intent): IBinder {
        return PlayerBinder()
    }

    private fun destroySession() {
        player.release()
        player = SimpleExoPlayer.Builder(this).build()
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
        queue.clear()
        history.clear()
        nowPlaying = null
    }

    override fun onCreate() {
        super.onCreate()
        player = SimpleExoPlayer.Builder(this).build()
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LocalBroadcastManager.getInstance(this).registerReceiver(destroyReceiver, IntentFilter(PlayerServiceEvents.DESTROY.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(newSongReceiver, IntentFilter(PlayerServiceEvents.NEW_SONG.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(playPauseReceiver, IntentFilter(PlayerServiceEvents.PLAY_PAUSE.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(skipNextReceiver, IntentFilter(PlayerServiceEvents.SKIP_NEXT.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(skipPrevReceiver, IntentFilter(PlayerServiceEvents.SKIP_PREV.name))
        LocalBroadcastManager.getInstance(this).registerReceiver(sessionDestroyReceiver, IntentFilter(PlayerServiceEvents.SESSION_DESTROY.name))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(destroyReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newSongReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playPauseReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(skipNextReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(skipPrevReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sessionDestroyReceiver)
    }

    fun connectPlayerView(playerView: PlayerView) {
        playerView.player = player
    }
}