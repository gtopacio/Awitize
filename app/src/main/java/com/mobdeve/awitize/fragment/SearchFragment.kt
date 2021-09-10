package com.mobdeve.awitize.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.mobdeve.awitize.R
import com.mobdeve.awitize.enums.DatabaseCollections
import com.mobdeve.awitize.model.Collection
import com.mobdeve.awitize.model.Music
import com.mobdeve.awitize.recyclerviews.CollectionAdapter
import com.mobdeve.awitize.recyclerviews.RecyclerAdapter
import com.mobdeve.awitize.service.PlayerService
import java.lang.RuntimeException
import kotlin.math.log

private const val TAG = "SearchFragment"

class SearchFragment : Fragment(), CollectionAdapter.MusicQueuer{

    private lateinit var recycler_view: RecyclerView
    private lateinit var recyclerAdapter: CollectionAdapter

    private lateinit var editText: EditText
    private var s : Editable? = null

    private var musicData = ArrayList<Music>()
    private var searchResults = ArrayList<Music>()

    private var valueEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if(s.toString() != ""){
                musicData.clear()
                snapshot.children.forEach{ data ->
                    if(data != null){
                        val title = data.child("title").value.toString()
                        val artist = data.child("artist").value.toString()
                        val audioFileURL = data.child("audioFileURL").value.toString()
                        val albumCoverURL = data.child("albumCoverURL").value.toString()
                        val banned : ArrayList<String> = ArrayList()
                        val bannedRegions = data.child("bannedRegions")
                        bannedRegions.children.forEach {
                            it?.key?.let { it1 -> banned.add(it1) }
                        }

                        if (title.replace("\\s".toRegex(), "").contains(s.toString(), ignoreCase = true) || artist.replace("\\s".toRegex(), "").contains(s.toString(), ignoreCase = true)){
                            musicData.add(Music(snapshot.key.toString(), title, artist, audioFileURL, albumCoverURL, banned))
                        }

                    }
                }
                recyclerAdapter.setSongs(musicData)
            }
        }
        override fun onCancelled(error: DatabaseError) {

        }
    }

    private var serviceBounded : Boolean = false
    private var playerService : PlayerService? = null
    private var conn = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceBounded = true
            playerService = (service as PlayerService.PlayerBinder).getService()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
            serviceBounded = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recycler_view = view.findViewById(R.id.rv_frag_results)
        recyclerAdapter = CollectionAdapter(this)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = recyclerAdapter
        }

        editText = view.findViewById(R.id.et_searchview)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                this@SearchFragment.s = s
                FirebaseDatabase.getInstance().getReference("music").addValueEventListener(valueEventListener)
            }
        })

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val i = Intent(context, PlayerService::class.java)
        context.bindService(i, conn, Context.BIND_AUTO_CREATE)
    }

    override fun onDetach() {
        super.onDetach()
        context?.unbindService(conn)
        recycler_view.apply {
            layoutManager = null
            adapter = null
        }
        FirebaseDatabase.getInstance().getReference("music").removeEventListener(valueEventListener)
    }

    override fun queueMusic(music: Music) {
        playerService?.queueSong(music)
    }

    override fun playImmediately(music: Music) {
        playerService?.playImmediately(music)
    }
}