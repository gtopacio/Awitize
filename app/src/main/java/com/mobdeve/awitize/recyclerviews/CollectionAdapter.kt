package com.mobdeve.awitize.recyclerviews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.awitize.R
import com.mobdeve.awitize.model.Music

class CollectionAdapter: RecyclerView.Adapter<CollectionAdapter.ViewHolder>(){

    private val TAG = "CollectionAdapter"
    private var songs : ArrayList<Music> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.artist.text = songs[position].artist
        holder.title.text = songs[position].title
        
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun setSongs(newSongs : ArrayList<Music>){
        songs = newSongs
        for(x in songs){
            Log.d(TAG, "setSongs: ${x.title} ${x.artist}")
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var artist: TextView = itemView.findViewById(R.id.tv_item_artist)
        var title: TextView = itemView.findViewById(R.id.tv_item_title)

        init{
            itemView.setOnClickListener {
                val position: Int = bindingAdapterPosition
                Toast.makeText(itemView.context, " ${position} clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}