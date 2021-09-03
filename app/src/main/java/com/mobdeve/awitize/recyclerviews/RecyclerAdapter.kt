package com.mobdeve.awitize.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.awitize.R
import com.mobdeve.awitize.model.Collection

class RecyclerAdapter(collectionListener: CollectionListener) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

//    private var genre = arrayOf("Genre 1", "Genre 2", "Genre 3")
//    private var genreSongs = arrayOf(69, 420, 69420)
//    private var artist = arrayOf("Artist 1", "Artist 2", "Artist 3")
//    private var artistSongs = arrayOf(1, 2, 3)
//    private var album = arrayOf("Album 1", "Album 2", "Album 3")
//    private var albumSongs = arrayOf(69696969, 12345, 15)

    private var displayedData = ArrayList<Collection>()
    private var collectionListener = collectionListener

    interface CollectionListener{
        fun onClickCollectionListener()
    }

    fun setData(newData : ArrayList<Collection>){
        displayedData = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
            return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.categoryName.text = displayedData.get(position).categoryName
        holder.categoryCount.text = displayedData.get(position).count.toString()
    }

    override fun getItemCount(): Int {
        return displayedData.size
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