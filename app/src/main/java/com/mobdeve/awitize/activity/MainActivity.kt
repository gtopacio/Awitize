package com.mobdeve.awitize.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.awitize.R
import com.mobdeve.awitize.enums.PlayerServiceEvents
import com.mobdeve.awitize.fragment.*
import com.mobdeve.awitize.helpers.DatabaseHelper
import com.mobdeve.awitize.recyclerviews.RecyclerAdapter
import com.mobdeve.awitize.model.Collection
import com.mobdeve.awitize.service.PlayerService

class MainActivity : AppCompatActivity(), AccountFragment.AccountListener, NavFragment.NavListener, HomeFragment.HomeListener, RecyclerAdapter.CollectionListener {

    private lateinit var libraryFragment: LibraryFragment
    private lateinit var homeFragment : HomeFragment
    private lateinit var accountFragment : AccountFragment
    private lateinit var searchFragment : SearchFragment
    private lateinit var navFragment: NavFragment

    private lateinit var collectionFragment: CollectionTemplateFragment

    private val databaseHelper : DatabaseHelper = DatabaseHelper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(FirebaseAuth.getInstance().currentUser == null){
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
            return
        }

        val i = Intent(this, PlayerService::class.java)
        ContextCompat.startForegroundService(this, i)

        homeFragment = HomeFragment(this)
        accountFragment = AccountFragment()
        searchFragment = SearchFragment()
        navFragment = NavFragment()
        libraryFragment = LibraryFragment(this)

        collectionFragment = CollectionTemplateFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main_nav, navFragment)
            replace(R.id.frag_main, homeFragment)
            commit()
        }
    }

    override fun logout() {
        sendBroadcast(Intent(PlayerServiceEvents.SESSION_DESTROY.name))
        FirebaseAuth.getInstance().signOut()
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun deleteAccount() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        databaseHelper.deleteUser(uid?:"")
        FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener{
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(PlayerServiceEvents.SESSION_DESTROY.name))
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun tapSearch() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main, searchFragment)
            commit()
        }
    }

    override fun tapAccount() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main, accountFragment)
            commit()
        }
    }

    override fun tapLibrary() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main, libraryFragment)
            commit()
        }
    }

    override fun tapHome() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main, homeFragment)
            commit()
        }
    }

    override fun onClickCollectionListener(collection: Collection) {
        collectionFragment.setDisplayedCollection(collection)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_main, collectionFragment)
            commit()
        }
    }

}