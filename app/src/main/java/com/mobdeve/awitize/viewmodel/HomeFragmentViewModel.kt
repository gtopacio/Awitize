package com.mobdeve.awitize.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobdeve.awitize.model.Collection

class HomeFragmentViewModel : ViewModel() {

    private var artists = ArrayList<Collection>()
    private var albums = ArrayList<Collection>()
    private var genres = ArrayList<Collection>()
    private var displayed = MutableLiveData<ArrayList<Collection>>(ArrayList())

    private var category = "Genre"

    val displayedData : LiveData<ArrayList<Collection>>
        get() = displayed

    fun init(){

        FirebaseDatabase.getInstance().getReference("artists").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                artists.clear()
                snapshot.children.forEach{ data ->
                    if(data != null){
                        val key = data.key
                        val count = data.childrenCount
                        artists.add(Collection(key?:"", count))
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
                        albums.add(Collection(key?:"", count))
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
                        genres.add(Collection(key?:"", count))
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
}