package com.mobdeve.awitize.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.awitize.R
import com.mobdeve.awitize.recyclerviews.DialogAdapter

class CustomDialog (context: Context, internal var adapter: DialogAdapter, private var key: String) : Dialog(context) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private lateinit var newPlaylist: EditText

    private lateinit var addBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_playlist)

        recyclerView = findViewById(R.id.rv_playlists)
        mLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = adapter

        addBtn = findViewById(R.id.btn_add_playlist)
        newPlaylist = findViewById(R.id.et_new_playlist)

        addBtn.setOnClickListener {
            val id = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseDatabase.getInstance().getReference("users/$id/playlists").child(newPlaylist.text.toString()).child(key).setValue(true)
            Toast.makeText(context,"Added song to " + newPlaylist.text.toString(), Toast.LENGTH_SHORT).show()
            this.dismiss()
        }

    }
}