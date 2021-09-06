package com.mobdeve.awitize.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobdeve.awitize.R
import com.mobdeve.awitize.model.Music
import com.mobdeve.awitize.recyclerviews.CollectionAdapter
import com.mobdeve.awitize.recyclerviews.RecyclerAdapter
import com.mobdeve.awitize.service.PlayerService
import com.mobdeve.awitize.viewmodel.HomeFragmentViewModel
import java.lang.RuntimeException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "HomeFragment"

class HomeFragment(collectionListener: RecyclerAdapter.CollectionListener) : Fragment(), CollectionAdapter.MusicQueuer {

    interface HomeListener{
        fun tapLibrary()
    }

    private var listener : HomeListener? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var attachedContext : Context? = null
    private lateinit var spinner: Spinner

    private lateinit var recycler_view: RecyclerView
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var viewModel : HomeFragmentViewModel

    private lateinit var fab : FloatingActionButton
    private var collectionListener: RecyclerAdapter.CollectionListener

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


    init {
        this.collectionListener = collectionListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recycler_view = view.findViewById(R.id.rv_frag_home)

        fab = view.findViewById(R.id.fab_frag_home)
        fab.setOnClickListener{
            listener?.tapLibrary()
        }

        collectionAdapter = CollectionAdapter(this)
        recyclerAdapter = RecyclerAdapter(collectionListener)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = recyclerAdapter
        }

        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        viewModel.init()

        viewModel.displayedData.observe(viewLifecycleOwner, Observer {
            recyclerAdapter.setData(it)
        })
        viewModel.recommendations.observe(viewLifecycleOwner, Observer {
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
                    viewModel.generateRecommendations()
                    recycler_view.adapter = collectionAdapter
                    return
                }
                recycler_view.adapter =  recyclerAdapter
                viewModel.setCategory(text)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.attachedContext = context
        if(context is HomeListener)
            listener = context
        else
            throw RuntimeException("$context must implement HomeListener")
        val i = Intent(context, PlayerService::class.java)
        context.bindService(i, conn, Context.BIND_AUTO_CREATE)
    }

    override fun onDetach() {
        super.onDetach()
        attachedContext = null
        listener = null
        context?.unbindService(conn)
    }

    override fun queueMusic(music: Music) {
        playerService?.queueSong(music)
    }

    /*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    */
}