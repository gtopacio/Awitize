package com.mobdeve.awitize.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.awitize.MusicPlayer
import com.mobdeve.awitize.R

class MainActivity : AppCompatActivity() {
    private var tvEmail : TextView? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvEmail = findViewById<TextView>(R.id.tv_email)
        tvEmail?.setText(FirebaseAuth.getInstance().currentUser?.email)

//        val i = Intent(this, MusicPlayer :: class.java)
//        startActivity(i)
//        finish()
    }
}