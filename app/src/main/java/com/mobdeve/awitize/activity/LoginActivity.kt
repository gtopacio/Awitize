package com.mobdeve.awitize.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.awitize.R

class LoginActivity : AppCompatActivity() {

    private var etEmail : EditText? = null
    private var etPassword : EditText? = null
    private var mAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        initComponents()
    }

    private fun initComponents(){
        etEmail = findViewById(R.id.et_login_email)
        etPassword = findViewById(R.id.et_login_pw)
        val btnLogin = findViewById<Button>(R.id.btn_login_login)
        val btnSignup = findViewById<Button>(R.id.btn_login_signup)

        btnLogin.setOnClickListener {
            val email = etEmail?.text
            val password = etPassword?.text
            mAuth?.signInWithEmailAndPassword(email.toString(), password.toString())?.addOnCompleteListener{
                if(it.isSuccessful){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this@LoginActivity, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}