package com.mobdeve.awitize.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.mobdeve.awitize.recyclerviews.CollectionAdapter
import com.mobdeve.awitize.viewmodel.CollectionFragmentViewModel

class CollectionTemplateFragment : Fragment() {

    private val TAG = "CollectionTemplateFragm"
    private lateinit var collectionName : TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var viewModel : CollectionFragmentViewModel
    private lateinit var displayedData : Collection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection_template, container, false)
        collectionName = view.findViewById(R.id.tv_collection)
        recyclerView = view.findViewById(R.id.rv_frag_songs)
        collectionAdapter = CollectionAdapter()
        recyclerView.apply {
            adapter = collectionAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        viewModel = ViewModelProvider(this).get(CollectionFragmentViewModel::class.java)
        viewModel.loadCollection(displayedData)

        viewModel.collectionName.observe(viewLifecycleOwner, Observer {
            collectionName.text = it.toString()
        })
        viewModel.displayedData.observe(viewLifecycleOwner, Observer {
            for(x in it){
                Log.d(TAG, "onCreateView: ${x.title} ${x.artist}")
            }
            collectionAdapter.setSongs(it)
        })
        return view
    }

    fun setDisplayedCollection(newCollection : Collection){
        displayedData = newCollection
    }
}