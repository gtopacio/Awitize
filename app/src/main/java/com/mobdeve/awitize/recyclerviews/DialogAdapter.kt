package com.mobdeve.awitize.recyclerviews

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.awitize.R
import com.mobdeve.awitize.dialogs.CustomDialog
import com.mobdeve.awitize.model.Collection

class DialogAdapter(private var playlists: ArrayList<Collection>, private var key: String) : RecyclerView.Adapter<DialogAdapter.ViewHolder>() {

    private var customDialog: CustomDialog? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: DialogAdapter.ViewHolder, position: Int) {
        holder.playlistName.text = playlists[position].categoryName
        holder.playlistSongCount.text = playlists[position].count.toString()
        if (position % 2 == 1) {
            holder.playlistCL.setBackgroundColor(Color.parseColor("#1C2120"))
        } else {
            holder.playlistCL.setBackgroundColor(Color.parseColor("#152D2E"))
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun setCustomDialog (customDialog: CustomDialog) {
        this.customDialog = customDialog
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var playlistName: TextView = itemView.findViewById(R.id.tv_category_name)
        var playlistSongCount: TextView = itemView.findViewById(R.id.tv_category_count)
        var playlistCL: ConstraintLayout = itemView.findViewById(R.id.cl_category)

        init {
            itemView.setOnClickListener {
                val id = FirebaseAuth.getInstance().currentUser?.uid
                FirebaseDatabase.getInstance().getReference("users/" + id + "/playlists/" + playlistName.text.toString()).child(key).setValue(true)
                Toast.makeText(itemView.context,"Added song to " + playlistName.text.toString(),Toast.LENGTH_SHORT).show()
                customDialog?.dismiss()
                customDialog = null
            }
        }
    }
}