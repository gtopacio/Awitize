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
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
}