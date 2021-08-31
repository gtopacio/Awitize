package com.mobdeve.awitize.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.awitize.MusicPlayer
import com.mobdeve.awitize.R

class MainActivity : AppCompatActivity() {
    //private var tvEmail : TextView? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //tvEmail = findViewById<TextView>(R.id.tv_email)
        //tvEmail?.setText(FirebaseAuth.getInstance().currentUser?.email)

//        val i = Intent(this, MusicPlayer :: class.java)
//        startActivity(i)
//        finish()
        /*
        val categories = resources.getStringArray(R.array.Categories)
        val spinner = findViewById<Spinner>(R.id.sp_main_category)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, categories)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    Toast.makeText(this@MainActivity,
                        "Selected: " + " " +
                                "" + categories[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }*/
    }
}