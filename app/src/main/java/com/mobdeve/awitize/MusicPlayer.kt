package com.mobdeve.awitize

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class MusicPlayer : AppCompatActivity() {
    private var TAG = "MusicPlayer"
    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null
    private var musicS : MediaItem?=null

    var receiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive: " + musicS?.toString())
            player?.setMediaItem(musicS?: MediaItem.fromUri(""))
            player?.prepare()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("New Song"))
        setContentView(R.layout.activity_music_player)
        player = SimpleExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.pv_player)
        playerView?.setPlayer(player)
        player?.setPlayWhenReady(true)
        player?.addListener(object: Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if(player?.isPlaying()?:false){
                    var sec = player?.duration?.div(1000)
                    val min: Long = sec?.div(60) ?: 0
                    sec = sec?.minus(min * 60)

                    Log.d(TAG, "onPlaybackStateChanged: Duration - " + min + " mins " + sec + "sec")
                }
            }
        })
        var executor = Executors.newSingleThreadExecutor()
        executor.execute(Runnable {
            var metaData = MediaMetadata.Builder()
            metaData.setMediaUri(Uri.parse("https://drive.google.com/uc?id=1E9alcqJwVotLNx-uzVE7QWSIuFk6WJkI"))
            metaData.setAlbumTitle("2017")
            metaData.setArtworkUri(Uri.parse("https://drive.google.com/uc?id=1-ZkHAIGn0SrpSQmEv3l1mbK8UgODfo9g"))
            metaData.setTitle("Beautiful Moon")
            metaData.setArtist("Noiseless-World")
            var url = URL("https://drive.google.com/uc?id=1-ZkHAIGn0SrpSQmEv3l1mbK8UgODfo9g")
            metaData.setArtworkData(url.readBytes(), MediaMetadata.PICTURE_TYPE_FRONT_COVER)
            var music = MediaItem.Builder().setMediaMetadata(metaData.build()).setUri("https://drive.google.com/uc?id=1E9alcqJwVotLNx-uzVE7QWSIuFk6WJkI").build()
            Log.d(TAG, "onCreate: " + music.mediaMetadata.mediaUri)
            musicS = music
            LocalBroadcastManager.getInstance(this@MusicPlayer).sendBroadcast(Intent("New Song"))
        })
    }
}