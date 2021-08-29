package com.mobdeve.awitize

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btn_login_login);
        val btnSignup = findViewById<Button>(R.id.btn_login_signup);

        btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }

        btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent);
        }
    }
}