package com.mobdeve.awitize.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobdeve.awitize.enums.DatabaseCollections
import com.mobdeve.awitize.helpers.DatabaseHelper
import com.mobdeve.awitize.model.Collection
import com.mobdeve.awitize.model.Music

class CollectionFragmentViewModel : ViewModel() {

    private val TAG = "CollectionFragmentViewM"

    private var data : ArrayList<Music> = ArrayList()
    private var name : MutableLiveData<String> = MutableLiveData("Collection")
    private var displayed : MutableLiveData<ArrayList<Music>> = MutableLiveData()

    val displayedData : LiveData<ArrayList<Music>>
        get() = displayed
    val collectionName : LiveData<String>
        get() = name

    fun loadCollection(newCollection: Collection) {
        name.value = newCollection.categoryName
        data = ArrayList()
        FirebaseDatabase.getInstance().getReference("${newCollection.parent}/${newCollection.categoryName}").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { song ->
                    FirebaseDatabase.getInstance().getReference("music/${song.key}").addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val title = snapshot.child("title").value.toString()
                            val artist = snapshot.child("artist").value.toString()
                            val audioFileURL = snapshot.child("audioFileURL").value.toString()
                            val albumCoverURL = snapshot.child("albumCoverURL").value.toString()
                            val banned : ArrayList<String> = ArrayList()
                            val bannedRegions = snapshot.child("bannedRegions")
                            bannedRegions.children.forEach {
                                it?.key?.let { it1 -> banned.add(it1) }
                            }
                            data.add(Music(snapshot.key.toString(), title, artist, audioFileURL, albumCoverURL, banned))
                            displayed.value = data
                        }
                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}