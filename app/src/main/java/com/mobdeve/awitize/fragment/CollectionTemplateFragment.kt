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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.awitize.R
import com.mobdeve.awitize.model.Collection
import com.mobdeve.awitize.model.Music
import com.mobdeve.awitize.recyclerviews.CollectionAdapter
import com.mobdeve.awitize.service.PlayerService
import com.mobdeve.awitize.viewmodel.CollectionFragmentViewModel

class CollectionTemplateFragment : Fragment(),  CollectionAdapter.MusicQueuer{

    private lateinit var collectionName : TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var collectionAdapter: CollectionAdapter
    private var viewModel : CollectionFragmentViewModel? = null
    private lateinit var displayedData : Collection
    private lateinit var editOption : ImageButton
    private lateinit var editPlaylistName : EditText

    private var editMode : Boolean = false

    //Service Connections
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val i = Intent(context, PlayerService::class.java)
        context.bindService(i, conn, Context.BIND_AUTO_CREATE)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection_template, container, false)
        collectionName = view.findViewById(R.id.tv_collection)
        recyclerView = view.findViewById(R.id.rv_frag_songs)
        collectionAdapter = CollectionAdapter(this)

        recyclerView.apply {
            adapter = collectionAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        viewModel = ViewModelProvider(this).get(CollectionFragmentViewModel::class.java)
        viewModel?.loadCollection(displayedData)

        viewModel?.collectionName?.observe(viewLifecycleOwner, Observer {
            collectionName.text = it.toString()
        })
        viewModel?.displayedData?.observe(viewLifecycleOwner, Observer {
            collectionAdapter.setSongs(it)
        })

        editOption = view.findViewById(R.id.ib_delete_songs)
        editPlaylistName = view.findViewById(R.id.et_edit_playlist)

        if (displayedData.playlist)
            editOption.visibility = (View.VISIBLE)
        else
            editOption.visibility = (View.GONE)

        editOption.setOnClickListener {
            editMode = !editMode
            if (editMode) {
                editOption.setImageResource(R.drawable.ic___check_vector)
                editPlaylistName.visibility = (View.VISIBLE)
                editPlaylistName.setHint(collectionName.text)
                collectionName.visibility = (View.INVISIBLE)
            } else {
                editOption.setImageResource(R.drawable.ic___settings_vector)
                editPlaylistName.visibility = (View.INVISIBLE)
                collectionName.visibility = (View.VISIBLE)

                collectionName.text = editPlaylistName.text
                //FirebaseDatabase.getInstance().getReference("users/$id/playlists/$playlistName").setValue(null)
            }
            collectionAdapter.showDelete(displayedData.categoryName, editMode)
        }

        return view
    }

    override fun onDetach() {
        super.onDetach()
        context?.unbindService(conn)
        viewModel = null
        recyclerView.apply {
            adapter = null
            layoutManager = null
        }
    }

    fun setDisplayedCollection(newCollection : Collection){
        displayedData = newCollection
    }

    override fun queueMusic(music: Music) {
        playerService?.queueSong(music)
    }

    override fun playImmediately(music: Music) {
        playerService?.playImmediately(music)
    }

}