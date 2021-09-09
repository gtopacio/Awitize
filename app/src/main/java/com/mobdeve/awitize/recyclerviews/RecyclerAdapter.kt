package com.mobdeve.awitize.recyclerviews

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.awitize.R
import com.mobdeve.awitize.model.Collection

class RecyclerAdapter(collectionListener: CollectionListener) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var displayedData = ArrayList<Collection>()
    private var collectionListener = collectionListener

    interface CollectionListener {
        fun onClickCollectionListener(collection: Collection)
    }

    fun setData(newData: ArrayList<Collection>) {
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
        if (position % 2 == 1) {
            holder.categoryCL.setBackgroundColor(Color.parseColor("#1C2120"))
        } else {
            holder.categoryCL.setBackgroundColor(Color.parseColor("#152D2E"))
        }
    }

    override fun getItemCount(): Int {
        return displayedData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryName: TextView
        var categoryCount: TextView
        var categoryCL: ConstraintLayout
        init {
            categoryName = itemView.findViewById(R.id.tv_category_name)
            categoryCount = itemView.findViewById(R.id.tv_category_count)
            categoryCL = itemView.findViewById(R.id.cl_category)


            itemView.setOnClickListener {
                val position: Int = bindingAdapterPosition
                collectionListener.onClickCollectionListener(displayedData[position])
            }


        }


    }

}