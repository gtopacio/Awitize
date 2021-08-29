package com.mobdeve.awitize

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnSignup = findViewById<Button>(R.id.btn_signup_signup);
        val btnLogin = findViewById<Button>(R.id.btn_signup_login);

        btnSignup.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent);
        }
    }
}