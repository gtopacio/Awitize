package com.mobdeve.awitize.fragment

import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.mobdeve.awitize.R
import com.mobdeve.awitize.activity.MusicPlayerActivity
import com.mobdeve.awitize.enums.PlayerServiceEvents
import com.mobdeve.awitize.model.Music
import com.mobdeve.awitize.service.PlayerService

class PlayerFragment : Fragment() {

    //Components
    private lateinit var playPauseButton : ImageButton
    private lateinit var title : TextView
    private lateinit var artist : TextView
    private lateinit var albumCover : ImageView
    private lateinit var layout : ConstraintLayout

    //Service Connections
    private var serviceBounded : Boolean = false
    private var playerService : PlayerService? = null
    private var conn = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceBounded = true
            playerService = (service as PlayerService.PlayerBinder).getService()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
            serviceBounded = false
        }
    }

    //Broadcast Receivers
    private val stateChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateUI()
        }
    }

    private fun updateUI() {
        val metaData = playerService?.getNowPlaying()?.mediaMetadata
        context?.let { Glide.with(it).load(metaData?.artworkUri).error(R.drawable.logo___awitize).into(albumCover) }
        artist.text = if(metaData == null) "No Artist" else metaData.artist
        title.text = if(metaData == null) "No Song" else metaData.title
        playPauseButton.setBackgroundResource(if(playerService?.isPlaying() == true) R.drawable.ic___pause_vector else R.drawable.ic___play_vector)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        playPauseButton = view.findViewById(R.id.ib_mp_play)
        title = view.findViewById(R.id.tv_mp_title)
        artist = view.findViewById(R.id.tv_mp_artist)
        albumCover = view.findViewById(R.id.iv_mp_albumart)
        layout = view.findViewById(R.id.cl_frag_mp)
        layout.setOnClickListener{
            val i = Intent(view.context, MusicPlayerActivity::class.java)
            startActivity(i)
        }
        playPauseButton.setOnClickListener{
            context?.sendBroadcast(Intent(PlayerServiceEvents.PLAY_PAUSE.name))
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val i = Intent(context, PlayerService::class.java)
        context.bindService(i, conn, Context.BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(context).registerReceiver(stateChangeReceiver, IntentFilter(PlayerServiceEvents.PLAYER_STATE_CHANGED.name))
    }

    override fun onDetach() {
        super.onDetach()
        context?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(stateChangeReceiver) }
        context?.unbindService(conn)
    }

}