package com.mobdeve.awitize.activity

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.mobdeve.awitize.R
import com.mobdeve.awitize.helpers.LocationHelper
import com.mobdeve.awitize.service.PlayerService

@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {

    private val TAG = "SplashScreenActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // make full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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
            LocationHelper.init(applicationContext)
            val playerIntent = Intent(this, PlayerService::class.java)
            ContextCompat.startForegroundService(this, playerIntent)
            Handler().postDelayed({
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
        Log.d(TAG, "onRequestPermissionsResult: ${grantResults[0] == PackageManager.PERMISSION_GRANTED}")
        if(grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED){
            LocationHelper.init(applicationContext)
            val playerIntent = Intent(this, PlayerService::class.java)
            ContextCompat.startForegroundService(this, playerIntent)
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }
        else{
            finish()
        }
    }
}