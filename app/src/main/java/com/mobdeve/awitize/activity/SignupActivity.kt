package com.mobdeve.awitize.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.awitize.R
import com.mobdeve.awitize.helpers.DatabaseHelper

class SignupActivity : AppCompatActivity() {

    private lateinit var etEmail : EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val db : DatabaseHelper = DatabaseHelper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        initComponents()
    }

    private fun initComponents(){
        etEmail = findViewById(R.id.et_signup_email)
        etPassword = findViewById(R.id.et_signup_pw)
        etConfirmPassword = findViewById(R.id.et_signup_cfm_pw)

        val btnSignup = findViewById<Button>(R.id.btn_signup_signup)
        val btnLogin = findViewById<Button>(R.id.btn_signup_login)
        btnSignup.setOnClickListener {
            val email: String = etEmail.text.toString()
            val password: String = etPassword.text.toString()
            val cfmPassword: String = etConfirmPassword.text.toString()

            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this, "Invalid Email Input", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(password.length < 8){
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(password != cfmPassword){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                if(it.isSuccessful){
                    db.registerUser(email, mAuth.currentUser?.uid.toString())
                    val i = Intent(this@SignupActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
                else{
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}