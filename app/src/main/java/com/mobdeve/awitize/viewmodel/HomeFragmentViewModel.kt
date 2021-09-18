package com.mobdeve.awitize.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobdeve.awitize.helpers.LocationHelper
import com.mobdeve.awitize.model.Collection
import com.mobdeve.awitize.model.Music
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class HomeFragmentViewModel : ViewModel() {

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
                val worker = Runnable {
                    artists.clear()
                    snapshot.children.forEach{ data ->
                        if(data != null){
                            val key = data.key
                            val count = data.childrenCount
                            artists.add(Collection("artists", key?:"", count, false))
                        }
                    }
                    if(category == "Artist"){
                        displayed.postValue(artists)
                    }
                }
                Executors.newSingleThreadExecutor().execute(worker)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        FirebaseDatabase.getInstance().getReference("albums").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val worker = Runnable {
                    albums.clear()
                    snapshot.children.forEach{ data ->
                        if(data != null){
                            val key = data.key
                            val count = data.childrenCount
                            albums.add(Collection("albums",key?:"", count, false))
                        }
                    }
                    if(category == "Album"){
                        displayed.postValue(albums)
                    }
                }
                Executors.newSingleThreadExecutor().execute(worker)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        FirebaseDatabase.getInstance().getReference("genres").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val worker = Runnable {
                    genres.clear()
                    snapshot.children.forEach{ data ->
                        if(data != null){
                            val key = data.key
                            val count = data.childrenCount
                            genres.add(Collection("genres",key?:"", count, false))
                        }
                    }
                    if(category == "Genre"){
                        displayed.postValue(genres)
                    }
                }
                Executors.newSingleThreadExecutor().execute(worker)
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

    fun generateRecommendations(context : Context) {
        val rec : ArrayList<Music> = ArrayList()
        val currentCountry = LocationHelper.getInstance(context)?.currentCountry?.value
        FirebaseDatabase.getInstance().getReference("music").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val worker = object: Runnable{
                    override fun run() {
                        val rand : LinkedList<String> = LinkedList()
                        for(x in snapshot.children){
                            x.key?.let { rand.add(it) }
                        }
                        rand.shuffle()
                        val randSize = rand.size - 10
                        while(rand.isNotEmpty() && rand.size > randSize){
                            FirebaseDatabase.getInstance().getReference("music/${rand.pollFirst()}").get().addOnCompleteListener {
                                if(it.isSuccessful){
                                    val snapshotData = it.result
                                    val bannedRegions = snapshotData.child("bannedRegions")
                                    val banned : ArrayList<String> = ArrayList()
                                    bannedRegions.children.forEach {region ->
                                        region?.key?.let { it1 -> banned.add(it1) }
                                    }

                                    if(banned.isNotEmpty() && currentCountry == null){
                                        return@addOnCompleteListener
                                    }

                                    if(banned.contains(currentCountry?:"")){
                                        return@addOnCompleteListener
                                    }

                                    val title = snapshotData.child("title").value.toString()
                                    val artist = snapshotData.child("artist").value.toString()
                                    val audioFileURL = snapshotData.child("audioFileURL").value.toString()
                                    val albumCoverURL = snapshotData.child("albumCoverURL").value.toString()
                                    val audioURI = snapshotData.child("audioURI").value.toString()
                                    val albumURI = snapshotData.child("albumURI").value.toString()

                                    rec.add(Music(snapshotData.key.toString(), title, artist, audioFileURL, audioURI, albumCoverURL, albumURI, banned))
                                    recom.postValue(rec)
                                }
                            }
                        }
                    }
                }
                Executors.newSingleThreadExecutor().execute(worker)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}