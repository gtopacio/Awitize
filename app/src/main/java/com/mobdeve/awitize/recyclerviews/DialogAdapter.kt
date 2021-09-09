package com.mobdeve.awitize.recyclerviews

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.awitize.R

class DialogAdapter : RecyclerView.Adapter<DialogAdapter.ViewHolder>() {

    private val samplePlaylist = arrayOf("P1", "P2", "P3", "P4")
    private val samplePlaylistcount = arrayOf("1", "2", "3", "4")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: DialogAdapter.ViewHolder, position: Int) {
        holder.playlistName.text = samplePlaylist.get(position)
        holder.playlistSongCount.text = samplePlaylistcount.get(position)
        if (position % 2 == 1) {
            holder.playlistCL.setBackgroundColor(Color.parseColor("#1C2120"))
        } else {
            holder.playlistCL.setBackgroundColor(Color.parseColor("#152D2E"))
        }
    }

    override fun getItemCount(): Int {
        return samplePlaylist.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var playlistName: TextView = itemView.findViewById(R.id.tv_category_name)
        var playlistSongCount: TextView = itemView.findViewById(R.id.tv_category_count)
        var playlistCL: ConstraintLayout = itemView.findViewById(R.id.cl_category)
    }
}