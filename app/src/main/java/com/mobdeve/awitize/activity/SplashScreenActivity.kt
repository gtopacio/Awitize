package com.mobdeve.awitize.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.mobdeve.awitize.R
import com.mobdeve.awitize.helpers.LocationHelper

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        else{
            LocationHelper.getInstance(applicationContext)
            Handler(Looper.getMainLooper()).postDelayed({
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
            }, 3000) // 3 seconds
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED){
            LocationHelper.getInstance(applicationContext)
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }
        else{
            finish()
        }
    }
}