package com.mobdeve.awitize.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.awitize.R

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

        private var genre = arrayOf("Genre 1", "Genre 2", "Genre 3")
        private var genreSongs = arrayOf(69, 420, 69420)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
            holder.categoryName.text = genre[position]
            holder.categoryCount.text = genreSongs[position].toString()
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
            }


        }


    }