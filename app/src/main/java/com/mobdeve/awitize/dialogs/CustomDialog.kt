package com.mobdeve.awitize.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.awitize.R
import com.mobdeve.awitize.recyclerviews.DialogAdapter

class CustomDialog (context: Context, internal var adapter: DialogAdapter) : Dialog(context) {

    var dialog: Dialog? = null

    private lateinit var recycler_view: RecyclerView
    private lateinit var recyclerAdapter: DialogAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private lateinit var newPlaylist: EditText

    private lateinit var addBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_playlist)

        recycler_view = findViewById(R.id.rv_playlists)
        mLayoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = mLayoutManager
        recycler_view.adapter = adapter
    }
}