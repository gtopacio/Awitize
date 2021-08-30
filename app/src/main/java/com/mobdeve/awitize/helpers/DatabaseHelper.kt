package com.mobdeve.awitize.helpers

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.awitize.enums.DatabaseCollections
import com.mobdeve.awitize.model.User

class DatabaseHelper {

    private val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val users : DatabaseReference = db.reference.child(DatabaseCollections.users.name)
    private val musics : DatabaseReference = db.reference.child(DatabaseCollections.musics.name)

    public fun registerUser(context: Context, email: String, uid:String){
        val user = User(email)
        users.child(uid).setValue(user)
    }

    public fun deleteUser(context: Context, uid: String){
        users.child(uid).removeValue()
    }

}