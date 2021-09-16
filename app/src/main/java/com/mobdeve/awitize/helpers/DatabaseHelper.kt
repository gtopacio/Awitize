package com.mobdeve.awitize.helpers

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.awitize.model.User

class DatabaseHelper {

    private val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val users : DatabaseReference = db.reference.child("users")

    fun registerUser(email: String, uid:String){
        val user = User(email)
        users.child(uid).setValue(user)
    }

    fun deleteUser(uid: String){
        users.child(uid).removeValue()
    }

    companion object{
        private var instance: DatabaseHelper? = null
        fun getInstance(): DatabaseHelper = getIns()
        private fun getIns() : DatabaseHelper{
            if(instance == null){
                this.instance = DatabaseHelper()
            }
            return instance as DatabaseHelper
        }
    }
}