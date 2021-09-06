package com.mobdeve.awitize.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobdeve.awitize.model.Collection
import com.mobdeve.awitize.model.Music
import java.util.*
import kotlin.collections.ArrayList

class HomeFragmentViewModel : ViewModel() {

    private val TAG = "HomeFragmentViewModel"

    private var artists = ArrayList<Collection>()
    private var albums = ArrayList<Collection>()
    private var genres = ArrayList<Collection>()
    private var displayed = MutableLiveData<ArrayList<Collection>>(ArrayList())
    private var recom = MutableLiveData<ArrayList<Music>>(ArrayList())

    private var category = "Genre"

    val displayedData : LiveData<ArrayList<Collection>>
        get() = displayed

    val recommendations : LiveData<ArrayList<Music>>
        get() = recom

    fun init(){

        FirebaseDatabase.getInstance().getReference("artists").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                artists.clear()
                snapshot.children.forEach{ data ->
                    if(data != null){
                        val key = data.key
                        val count = data.childrenCount
                        artists.add(Collection("artists", key?:"", count))
                    }
                }
                if(category == "Artist"){
                    displayed.value = artists
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        FirebaseDatabase.getInstance().getReference("albums").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                albums.clear()
                snapshot.children.forEach{ data ->
                    if(data != null){
                        val key = data.key
                        val count = data.childrenCount
                        albums.add(Collection("albums",key?:"", count))
                    }
                }
                if(category == "Album"){
                    displayed.value = albums
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        FirebaseDatabase.getInstance().getReference("genres").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                genres.clear()
                snapshot.children.forEach{ data ->
                    if(data != null){
                        val key = data.key
                        val count = data.childrenCount
                        genres.add(Collection("genres",key?:"", count))
                    }
                }
                if(category == "Genre"){
                    displayed.value = genres
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun setCategory(newCategory : String){
        category = newCategory
        if(newCategory == "Artist"){
            displayed.value = artists
            return
        }

        if(newCategory == "Album"){
            displayed.value = albums
            return
        }

        if(newCategory == "Genre"){
            displayed.value = genres
            return
        }
    }

    fun generateRecommendations() {
        val rec : ArrayList<Music> = ArrayList()
        FirebaseDatabase.getInstance().getReference("music").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val rand : LinkedList<String> = LinkedList()
                for(x in snapshot.children){
                    x.key?.let { rand.add(it) }
                }
                rand.shuffle()
                for(i in 0..10){
                    FirebaseDatabase.getInstance().getReference("music/${rand.pollFirst()}").get().addOnCompleteListener {
                        if(it.isSuccessful){
                            val snapshot = it.result
                            val title = snapshot.child("title").value.toString()
                            val artist = snapshot.child("artist").value.toString()
                            val audioFileURL = snapshot.child("audioFileURL").value.toString()
                            val albumCoverURL = snapshot.child("albumCoverURL").value.toString()
                            val banned : ArrayList<String> = ArrayList()
                            val bannedRegions = snapshot.child("bannedRegions")
                            bannedRegions.children.forEach {
                                it?.key?.let { it1 -> banned.add(it1) }
                            }
                            rec.add(Music(title, artist, audioFileURL, albumCoverURL, banned))
                            recom.value = rec
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}