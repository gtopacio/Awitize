package com.mobdeve.awitize.recyclerviews

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobdeve.awitize.R
import com.mobdeve.awitize.dialogs.CustomDialog
import com.mobdeve.awitize.helpers.LocationHelper
import com.mobdeve.awitize.model.Collection
import com.mobdeve.awitize.model.Music
import java.util.concurrent.Executors

class CollectionAdapter(private var queuer: MusicQueuer?) :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    interface MusicQueuer{
        fun queueMusic(music: Music)
        fun playImmediately(music: Music)
    }

    private val TAG = "CollectionAdapter"
    private var songs : ArrayList<Music> = ArrayList()
    private var currentLocation : String? = null

    private lateinit var playlistName : String
    private var delete = false

    fun showDelete (playlistName: String, delete: Boolean) {
        this.playlistName = playlistName
        this.delete = delete
        notifyDataSetChanged()
    }

    fun setCurrentLocation(newLocation: String?){
        currentLocation = newLocation
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (delete)
            holder.songDelete.visibility = View.VISIBLE
        else
            holder.songDelete.visibility = View.GONE

        holder.artist.text = songs[position].artist
        holder.title.text = songs[position].title

        if (position % 2 == 1) {
            holder.conslay.setBackgroundColor(Color.parseColor("#1C2120"))
        }
        else {
            holder.conslay.setBackgroundColor(Color.parseColor("#152D2E"))
        }

        songs[position].banned.forEach{region ->
            if (songs[position].banned.size > 0 && (currentLocation == null || currentLocation == "")) {
                holder.conslay.setBackgroundColor(Color.parseColor("#8D8F84"))
                return
            }

            if (songs[position].banned.indexOf(currentLocation) > -1) {
                holder.conslay.setBackgroundColor(Color.parseColor("#8D8F84"))
                return
            }

        }
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
        var conslay: ConstraintLayout = itemView.findViewById(R.id.item_cl_song)
        var queue: ImageButton = itemView.findViewById(R.id.ib_song_queue)
        var play: ImageButton = itemView.findViewById(R.id.ib_song_play)
        var ibSongPlaylist: ImageButton = itemView.findViewById(R.id.ib_song_playlist)
        var songDelete: ImageButton = itemView.findViewById(R.id.ib_song_delete)

        init{

            play.setOnClickListener{
                this@CollectionAdapter.queuer?.playImmediately(songs[bindingAdapterPosition])
            }

            queue.setOnClickListener {
                this@CollectionAdapter.queuer?.queueMusic(songs[bindingAdapterPosition])
            }

            ibSongPlaylist.setOnClickListener {
                var playlists = ArrayList<Collection>()
                val id = FirebaseAuth.getInstance().currentUser?.uid
                var dialogAdapter: DialogAdapter
                var customDialog: CustomDialog
                FirebaseDatabase.getInstance().getReference("users/" + id + "/playlists").addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        playlists.clear()
                        snapshot.children.forEach{ data ->
                            if(data != null){
                                val key = data.key
                                val count = data.childrenCount
                                playlists.add(Collection("playlists",key?:"", count, true))
                                Log.d(TAG, key.toString())
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })

                dialogAdapter = DialogAdapter(playlists, songs[bindingAdapterPosition].key)
                customDialog = CustomDialog( itemView.context, dialogAdapter, songs[bindingAdapterPosition].key)
                dialogAdapter.setCustomDialog(customDialog)
                customDialog.show()
            }

            songDelete.setOnClickListener {
                val position: Int = bindingAdapterPosition
                var songKey = songs[position].key
                songs.removeAt(position)

                val id = FirebaseAuth.getInstance().currentUser?.uid
                FirebaseDatabase.getInstance().getReference("users/$id/playlists/$playlistName/$songKey").setValue(null)
                Toast.makeText(itemView.context,"Deleted song: " + title.text + " from " + playlistName, Toast.LENGTH_SHORT).show()

                notifyDataSetChanged()
            }
        }
    }
}