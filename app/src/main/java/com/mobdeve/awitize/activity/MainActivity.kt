package com.mobdeve.awitize.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.awitize.MusicPlayer
import com.mobdeve.awitize.R
import com.mobdeve.awitize.fragment.*
import com.mobdeve.awitize.helpers.DatabaseHelper

class MainActivity : AppCompatActivity(), AccountFragment.AccountListener, NavFragment.NavListener, HomeFragment.HomeListener, LibraryFragment.LibraryListener {

    private val TAG = "MainActivity"

    private lateinit var libraryFragment: LibraryFragment
    private lateinit var homeFragment : HomeFragment
    private lateinit var accountFragment : AccountFragment
    private lateinit var searchFragment : SearchFragment
    private lateinit var navFragment: NavFragment

    private val databaseHelper : DatabaseHelper = DatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeFragment = HomeFragment()
        accountFragment = AccountFragment()
        searchFragment = SearchFragment()
        navFragment = NavFragment()
        libraryFragment = LibraryFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main_nav, navFragment)
            replace(R.id.frag_main, homeFragment)
            commit()
        }
    }

    override fun logout() {
        FirebaseAuth.getInstance().signOut()
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun deleteAccount() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        databaseHelper.deleteUser(this, uid?:"")
        FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener{
            if(it.isSuccessful){
                FirebaseAuth.getInstance().signOut()
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
            }
            else{
                Log.d(TAG, "deleteAccount: Unable to delete ${it.exception?.message}")
            }
        }
    }

    override fun tapSearch() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main, searchFragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun tapAccount() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main, accountFragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun tapBack() {
        super.onBackPressed()
    }

    override fun tapLibrary() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main, libraryFragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun tapHome() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main, homeFragment)
            addToBackStack(null)
            commit()
        }
    }
}