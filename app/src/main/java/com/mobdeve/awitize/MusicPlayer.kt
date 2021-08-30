package com.mobdeve.awitize

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.*
import com.mobdeve.awitize.enums.PlayerServiceEvents
import com.mobdeve.awitize.model.Music
import com.mobdeve.awitize.service.PlayerService

class MusicPlayer : AppCompatActivity() {
    private var TAG = "MusicPlayer"

    private lateinit var play : ImageButton
    private lateinit var skipNext : ImageButton
    private lateinit var skipPrev : ImageButton
    private lateinit var title : TextView
    private lateinit var btnSong1 : Button
    private lateinit var btnSong2 : Button

//    private var playerView: PlayerView? = null
//    private var player: SimpleExoPlayer? = null
//    private var musicS : MediaItem?=null
//    var receiver = object: BroadcastReceiver(){
//        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.d(TAG, "onReceive: " + musicS?.toString())
//            player?.setMediaItem(musicS?: MediaItem.fromUri(""))
//            player?.prepare()
//        }
//    }
    private lateinit var playerService: PlayerService
    private var serviceBounded = false
    private val conn = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playerService = (service as PlayerService.PlayerBinder).getService()!!
            this@MusicPlayer.serviceBounded = true
            Log.d(TAG, "onServiceConnected: Service Bounded")
            updateUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBounded = false
            Log.d(TAG, "onServiceDisconnected: Service Disconnected")
        }
    }

    private val stateChangeListener = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateUI()
        }
    }

    private fun updateUI() {
        val nowPlaying = playerService.getNowPlaying()
        val playing = playerService.isPlaying()

        if(playing)
            play.setImageResource(R.drawable.exo_controls_pause)
        else
            play.setImageResource(R.drawable.exo_controls_play)

        if(nowPlaying == null){
            title.text = "No Song"

            return
        }

        title.text = nowPlaying.mediaMetadata.artist.toString() + " - " + nowPlaying.mediaMetadata.title.toString()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player2)

        btnSong1 = findViewById(R.id.btn_queue_song1)
        btnSong2 = findViewById(R.id.btn_queue_song2)
        title = findViewById(R.id.tv_title)
        play = findViewById(R.id.ib_play)
        skipNext = findViewById(R.id.ib_skip_next)
        skipPrev = findViewById(R.id.ib_skip_prev)

        val playerIntent = Intent(this, PlayerService::class.java)
        bindService(playerIntent, conn, Context.BIND_AUTO_CREATE)

        play.setOnClickListener{
            val i = Intent(PlayerServiceEvents.PLAY_PAUSE.name)
            LocalBroadcastManager.getInstance(this).sendBroadcast(i)
        }

        skipNext.setOnClickListener{
            val i = Intent(PlayerServiceEvents.SKIP_NEXT.name)
            LocalBroadcastManager.getInstance(this).sendBroadcast(i)
        }

        skipPrev.setOnClickListener{
            val i = Intent(PlayerServiceEvents.SKIP_PREV.name)
            LocalBroadcastManager.getInstance(this).sendBroadcast(i)
        }

        btnSong1.setOnClickListener{
            if(serviceBounded){
                playerService.queueSong(Music("Beautiful Moon","Noiseless-World","https://drive.google.com/uc?id=1E9alcqJwVotLNx-uzVE7QWSIuFk6WJkI", "https://drive.google.com/uc?id=1-ZkHAIGn0SrpSQmEv3l1mbK8UgODfo9g"))
            }
            else{
                Toast.makeText(this, "Not Service Bounded", Toast.LENGTH_SHORT).show()
            }
        }

        btnSong2.setOnClickListener{
            if(serviceBounded){
                playerService.queueSong(Music("Fireworks","Noiseless-World","https://drive.google.com/uc?id=1TZy6fNL8nId1X0EqhRfDFVuCT0EjtNuy", "https://drive.google.com/uc?id=1-ZkHAIGn0SrpSQmEv3l1mbK8UgODfo9g"))
            }
            else{
                Toast.makeText(this, "Not Service Bounded", Toast.LENGTH_SHORT).show()
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(stateChangeListener, IntentFilter(PlayerServiceEvents.PLAYER_STATE_CHANGED.name))

//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("New Song"))
//        player = SimpleExoPlayer.Builder(this).build()
//        playerView = findViewById(R.id.pv_player)
//        playerView?.setPlayer(player)
//        player?.setPlayWhenReady(true)
//        player?.addListener(object: Player.Listener{
//            override fun onPlaybackStateChanged(playbackState: Int) {
//                super.onPlaybackStateChanged(playbackState)
//                if(player?.isPlaying()?:false){
//                    var sec = player?.duration?.div(1000)
//                    val min: Long = sec?.div(60) ?: 0
//                    sec = sec?.minus(min * 60)
//
//                    Log.d(TAG, "onPlaybackStateChanged: Duration - " + min + " mins " + sec + "sec")
//                }
//            }
//        })
//        var executor = Executors.newSingleThreadExecutor()
//        executor.execute(Runnable {
//            var metaData = MediaMetadata.Builder()
//            metaData.setMediaUri(Uri.parse("https://drive.google.com/uc?id=1E9alcqJwVotLNx-uzVE7QWSIuFk6WJkI"))
//            metaData.setAlbumTitle("2017")
//            metaData.setArtworkUri(Uri.parse("https://drive.google.com/uc?id=1-ZkHAIGn0SrpSQmEv3l1mbK8UgODfo9g"))
//            metaData.setTitle("Beautiful Moon")
//            metaData.setArtist("Noiseless-World")
//            var url = URL("https://drive.google.com/uc?id=1-ZkHAIGn0SrpSQmEv3l1mbK8UgODfo9g")
//            metaData.setArtworkData(url.readBytes(), MediaMetadata.PICTURE_TYPE_FRONT_COVER)
//            var music = MediaItem.Builder().setMediaMetadata(metaData.build()).setUri("https://drive.google.com/uc?id=1E9alcqJwVotLNx-uzVE7QWSIuFk6WJkI").build()
//            Log.d(TAG, "onCreate: " + music.mediaMetadata.mediaUri)
//            musicS = music
//            LocalBroadcastManager.getInstance(this@MusicPlayer).sendBroadcast(Intent("New Song"))
//        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(conn)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stateChangeListener)
    }
}