package com.mobdeve.awitize.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ui.PlayerView
import com.mobdeve.awitize.R
import com.mobdeve.awitize.enums.PlayerServiceEvents
import com.mobdeve.awitize.model.Music
import com.mobdeve.awitize.service.PlayerService

class MusicPlayerActivity : AppCompatActivity() {

    private lateinit var title : TextView
    private lateinit var artist : TextView
    private lateinit var albumCover : ImageView
    private lateinit var play : ImageButton
    private lateinit var next : ImageButton
    private lateinit var prev : ImageButton
    private lateinit var playerView : PlayerView

    //Service Connections
    private var serviceBounded : Boolean = false
    private var playerService : PlayerService? = null
    private var conn = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceBounded = true
            playerService = (service as PlayerService.PlayerBinder).getService()
            playerService?.connectPlayerView(playerView)
            updateUI()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
            serviceBounded = false
            playerView.player = null
        }
    }

    //Broadcast Receivers
    private val stateChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)
        initComponents()
        val i = Intent(this, PlayerService::class.java)
        bindService(i, conn, Context.BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(this).registerReceiver(stateChangeReceiver, IntentFilter(PlayerServiceEvents.PLAYER_STATE_CHANGED.name))
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(conn)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stateChangeReceiver)
        playerView.player = null
    }

    private fun updateUI() {
        val metaData = playerService?.getNowPlaying()?.mediaMetadata
        artist.text = if(metaData == null) "No Artist" else metaData.artist
        title.text = if(metaData == null) "No Song" else metaData.title
        play.setImageResource(if(playerService?.isPlaying() == true) R.drawable.ic___pause else R.drawable.ic___play)
        playerView.showController()
        Glide.with(this).load(metaData?.artworkUri).error(R.drawable.logo___awitize).into(albumCover)
    }

    private fun initComponents(){
        title = findViewById(R.id.tv_player_title)
        artist = findViewById(R.id.tv_player_artist)
        albumCover = findViewById(R.id.iv_player_album)
        play = findViewById(R.id.ib_player_play)
        next = findViewById(R.id.ib_player_next)
        prev = findViewById(R.id.ib_player_prev)
        playerView = findViewById(R.id.playerView)

        play.setOnClickListener{
            val i = Intent(PlayerServiceEvents.PLAY_PAUSE.name)
            LocalBroadcastManager.getInstance(this).sendBroadcast(i)
        }

        next.setOnClickListener {
            val i = Intent(PlayerServiceEvents.SKIP_NEXT.name)
            LocalBroadcastManager.getInstance(this).sendBroadcast(i)
        }

        prev.setOnClickListener {
            val i = Intent(PlayerServiceEvents.SKIP_PREV.name)
            LocalBroadcastManager.getInstance(this).sendBroadcast(i)
        }
    }
}