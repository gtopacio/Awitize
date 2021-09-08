package com.mobdeve.awitize.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.mobdeve.awitize.R
import com.mobdeve.awitize.enums.DatabaseCollections
import com.mobdeve.awitize.model.Collection
import com.mobdeve.awitize.model.Music
import com.mobdeve.awitize.recyclerviews.RecyclerAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recycler_view: RecyclerView
    private lateinit var recyclerAdapter: RecyclerAdapter

    private lateinit var editText: EditText

    private var musicData = ArrayList<Music>()
    private var searchResults = ArrayList<Music>()

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        editText = view.findViewById(R.id.et_searchview)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                TODO("Not yet implemented")
            }

            override fun afterTextChanged(s: Editable?) {
                FirebaseDatabase.getInstance().getReference("music").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        musicData.clear()
                        snapshot.children.forEach{ data ->
                            if(data != null){
                                val title = snapshot.child("title").value.toString()
                                val artist = snapshot.child("artist").value.toString()
                                val audioFileURL = snapshot.child("audioFileURL").value.toString()
                                val albumCoverURL = snapshot.child("albumCoverURL").value.toString()
                                val banned : ArrayList<String> = ArrayList()
                                val bannedRegions = snapshot.child("bannedRegions")
                                bannedRegions.children.forEach {
                                    it?.key?.let { it1 -> banned.add(it1) }
                                }
                                musicData.add(Music(title, artist, audioFileURL, albumCoverURL, banned))
                            }
                        }

                        for (result in musicData)
                            if (result.title.replace("\\s".toRegex(), "").contains(s.toString(), ignoreCase = true))
                                searchResults.add(result)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

        }

        )

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}