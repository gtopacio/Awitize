package com.mobdeve.awitize.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.awitize.R
import com.mobdeve.awitize.model.Music

class CollectionAdapter(private var queuer: MusicQueuer?) :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    interface MusicQueuer{
        fun queueMusic(music: Music)
    }

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
        notifyDataSetChanged()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        queuer = null
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var artist: TextView = itemView.findViewById(R.id.tv_item_artist)
        var title: TextView = itemView.findViewById(R.id.tv_item_title)

        init{
            itemView.setOnClickListener {
                this@CollectionAdapter.queuer?.queueMusic(songs[bindingAdapterPosition])
            }
        }
    }
}