package com.mobdeve.awitize.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.awitize.R
import com.mobdeve.awitize.fragment.NavFragment

class RecyclerAdapter(input: String, collectionListener: CollectionListener) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var spinnerInput: String = input
    private var genre = arrayOf("Genre 1", "Genre 2", "Genre 3")
    private var genreSongs = arrayOf(69, 420, 69420)
    private var artist = arrayOf("Artist 1", "Artist 2", "Artist 3")
    private var artistSongs = arrayOf(1, 2, 3)
    private var album = arrayOf("Album 1", "Album 2", "Album 3")
    private var albumSongs = arrayOf(69696969, 12345, 15)

    interface CollectionListener{
        fun onClickCollectionListener()
    }

    private var collectionListener : RecyclerAdapter.CollectionListener? = collectionListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
            return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        if (spinnerInput == "Genre") {
            holder.categoryName.text = genre[position]
            holder.categoryCount.text = genreSongs[position].toString()
            /*
            holder.itemView.setOnClickListener {
                collectionListener?.onClickCollectionListener()
                Toast.makeText(holder.itemView.context, " ${genre[position]} clicked", Toast.LENGTH_SHORT).show()
            }*/
        }
        if (spinnerInput == "Artist") {
            holder.categoryName.text = artist[position]
            holder.categoryCount.text = artistSongs[position].toString()
        }
        if (spinnerInput == "Album") {
            holder.categoryName.text = album[position]
            holder.categoryCount.text = albumSongs[position].toString()
        }

    }


    override fun getItemCount(): Int {
        return genre.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryName: TextView
        var categoryCount: TextView

        init {
            categoryName = itemView.findViewById(R.id.tv_category_name)
            categoryCount = itemView.findViewById(R.id.tv_category_count)

            itemView.setOnClickListener {
                val position: Int = bindingAdapterPosition

                collectionListener?.onClickCollectionListener()
                Toast.makeText(itemView.context, " ${position} clicked", Toast.LENGTH_SHORT).show()
            }
        }


    }


}