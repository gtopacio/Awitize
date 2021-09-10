package com.mobdeve.awitize.recyclerviews

import android.media.browse.MediaBrowser
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import com.mobdeve.awitize.R
import java.util.*
import kotlin.collections.ArrayList

class QueueAdapter : RecyclerView.Adapter<QueueAdapter.ViewHolder>(){

    private var queue : LinkedList<MediaItem> = LinkedList()

    fun setSongs(songs: LinkedList<MediaItem>){
        this.queue = songs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.queue_song, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currSong = queue[position].mediaMetadata
        holder.song.text = "${currSong.artist} - ${currSong.title}"
    }

    override fun getItemCount(): Int {
        return queue.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var song: TextView = itemView.findViewById(R.id.tv_queue_song)
    }
}