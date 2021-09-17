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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.awitize.R
import com.mobdeve.awitize.helpers.LocationHelper
import com.mobdeve.awitize.model.Music
import com.mobdeve.awitize.recyclerviews.CollectionAdapter
import com.mobdeve.awitize.recyclerviews.RecyclerAdapter
import com.mobdeve.awitize.service.PlayerService
import com.mobdeve.awitize.viewmodel.HomeFragmentViewModel
import java.lang.RuntimeException

class HomeFragment(private var collectionListener: RecyclerAdapter.CollectionListener) : Fragment(), CollectionAdapter.MusicQueuer {

    interface HomeListener{
        fun tapLibrary()
    }

    private var listener : HomeListener? = null

    private lateinit var spinner: Spinner

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var viewModel : HomeFragmentViewModel

    private lateinit var fab : FloatingActionButton

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionAdapter = CollectionAdapter(this)
        recyclerAdapter = RecyclerAdapter(collectionListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.rv_frag_home)

        fab = view.findViewById(R.id.fab_frag_home)
        fab.setOnClickListener{
            listener?.tapLibrary()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = recyclerAdapter
        }

        LocationHelper.getInstance(view.context)?.currentCountry?.observe(viewLifecycleOwner, {
            collectionAdapter.setCurrentLocation(it)
        })

        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        viewModel.init()

        viewModel.displayedData.observe(viewLifecycleOwner, {
            recyclerAdapter.setData(it)
        })
        viewModel.recommendations.observe(viewLifecycleOwner, {
            collectionAdapter.setSongs(it)
        })

        val categories = resources.getStringArray(R.array.Categories)
        spinner = view.findViewById(R.id.sp_frag_home_category)
        val spinneradapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item, categories
        )
        spinner.adapter = spinneradapter
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val text: String = parent?.getItemAtPosition(position).toString()
                if(text == "Recommendation"){
                    view?.context?.let { viewModel.generateRecommendations(it) }
                    recyclerView.adapter = collectionAdapter
                    return
                }
                recyclerView.adapter =  recyclerAdapter
                viewModel.setCategory(text)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is HomeListener)
            listener = context
        else
            throw RuntimeException("$context must implement HomeListener")
        val i = Intent(context, PlayerService::class.java)
        context.bindService(i, conn, Context.BIND_AUTO_CREATE)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        context?.unbindService(conn)
        recyclerView.apply {
            layoutManager = null
            adapter = null
        }
    }

    override fun queueMusic(music: Music) {
        playerService?.queueSong(music)
    }

    override fun playImmediately(music: Music) {
        playerService?.queueSong(music)
    }
}