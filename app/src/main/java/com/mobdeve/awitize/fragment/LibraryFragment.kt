package com.mobdeve.awitize.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobdeve.awitize.R
import com.mobdeve.awitize.model.Collection
import com.mobdeve.awitize.model.Music
import com.mobdeve.awitize.recyclerviews.CollectionAdapter
import com.mobdeve.awitize.recyclerviews.RecyclerAdapter
import com.mobdeve.awitize.service.PlayerService
import java.lang.RuntimeException

class LibraryFragment(private var collectionListener: RecyclerAdapter.CollectionListener) : Fragment(), CollectionAdapter.MusicQueuer  {

    private lateinit var recycler_view: RecyclerView
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var recyclerAdapter: RecyclerAdapter

    private var editMode : Boolean = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        collectionAdapter = CollectionAdapter(this)
        recyclerAdapter = RecyclerAdapter(collectionListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        var playlists = ArrayList<Collection>()
        val id = FirebaseAuth.getInstance().currentUser?.uid
        var editOption : ImageButton = view.findViewById(R.id.ib_delete_playlists)

        FirebaseDatabase.getInstance().getReference("users/" + id + "/playlists").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                playlists.clear()
                snapshot.children.forEach{ data ->
                    if(data != null){
                        val key = data.key
                        val count = data.childrenCount
                        playlists.add(Collection("users/${id}/playlists",key?:"", count, true))
                        recyclerAdapter.setData(playlists)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        editOption.setOnClickListener {
            editMode = !editMode
            if (editMode)
                editOption.setImageResource(R.drawable.ic___check_vector)
            else
                editOption.setImageResource(R.drawable.ic___settings_vector)
            recyclerAdapter.showDelete(editMode)
        }


        recycler_view = view.findViewById(R.id.rv_frag_lib)


        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = recyclerAdapter
        }


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
    }

    override fun queueMusic(music: Music) {
        playerService?.queueSong(music)
    }

    override fun playImmediately(music: Music) {
        playerService?.playImmediately(music)
    }


}