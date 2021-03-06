package com.mobdeve.awitize.recyclerviews

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.awitize.R
import com.mobdeve.awitize.model.Collection

class RecyclerAdapter(private var collectionListener: CollectionListener) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var displayedData = ArrayList<Collection>()

    private var delete = false

    interface CollectionListener {
        fun onClickCollectionListener(collection: Collection)
    }

    fun setData(newData: ArrayList<Collection>) {
        displayedData = newData
        notifyDataSetChanged()
    }

    fun showDelete (delete: Boolean) {
        this.delete = delete
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        if (delete)
            holder.categoryDelete.visibility = View.VISIBLE
        else
            holder.categoryDelete.visibility = View.GONE

        holder.categoryName.text = displayedData[position].categoryName
        val trackCnt: String = displayedData[position].count.toString() + " tracks"
        holder.categoryCount.text = trackCnt
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
        var categoryName: TextView = itemView.findViewById(R.id.tv_category_name)
        var categoryCount: TextView = itemView.findViewById(R.id.tv_category_count)
        var categoryCL: ConstraintLayout = itemView.findViewById(R.id.cl_category)
        var categoryDelete: ImageButton = itemView.findViewById(R.id.ib_category_delete)

        init {

            itemView.setOnClickListener {
                val position: Int = bindingAdapterPosition
                collectionListener.onClickCollectionListener(displayedData[position])
            }

            categoryDelete.setOnClickListener {
                val position: Int = bindingAdapterPosition
                displayedData.removeAt(position)

                val id = FirebaseAuth.getInstance().currentUser?.uid
                FirebaseDatabase.getInstance().getReference("users/" + id + "/playlists/" + categoryName.text.toString()).setValue(null)
                Toast.makeText(itemView.context,"Deleted playlist: " + categoryName.text.toString(),Toast.LENGTH_SHORT).show()

                notifyDataSetChanged()
            }

        }
    }
}