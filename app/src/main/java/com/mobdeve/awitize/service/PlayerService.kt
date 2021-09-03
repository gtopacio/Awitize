package com.mobdeve.awitize.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.gms.location.*
import com.mobdeve.awitize.enums.PlayerServiceEvents
import com.mobdeve.awitize.model.Music
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Consumer

class PlayerService : Service() {

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
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var currentLocation : String? = null

    private var locationCallback = object: LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            val geocoder = Geocoder(this@PlayerService)
            val location = p0?.locations?.first()
            currentLocation =
                location?.latitude?.let { geocoder.getFromLocation(it, location.longitude, 1).first().countryName }
        }
    }

    private var locationRequest = LocationRequest.create()

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

    fun queueSong(music: Music){
        val queueThread = object: Thread(){
            override fun run() {
                Looper.prepare()
                val metaData = MediaMetadata.Builder()
                metaData.setTitle(music.title)
                metaData.setArtist(music.artist)
                metaData.setArtworkUri(Uri.parse(music.albumCoverURL))
                val mediaItem = MediaItem.Builder().setUri(music.audioFileURL).setMediaMetadata(metaData.build()).build()
                queue.add(mediaItem)
                val i = Intent(PlayerServiceEvents.NEW_SONG.name)
                i.putExtra("message", "Queued " + music.artist + " - " + music.title)
                LocalBroadcastManager.getInstance(this@PlayerService).sendBroadcast(i)
            }
        }
        queueThread.start()
    }

    @SuppressLint("MissingPermission")
    private fun playNextSong() {

        val geocoder = Geocoder(this)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if(it != null){
                val countryName = geocoder.getFromLocation(it.latitude, it.longitude, 1).first().countryName
                Log.d(TAG, "playNextSong: $countryName")
            }
        }

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

    fun isPlaying(): Boolean{
        return player.playWhenReady
    }

    fun getNowPlaying(): MediaItem?{
        return nowPlaying
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

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest.interval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

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