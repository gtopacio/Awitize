package com.mobdeve.awitize

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mobdeve.awitize.enums.PlayerServiceEvents
import com.mobdeve.awitize.service.PlayerService

class Awitize : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("Awitize", "Awitize", importance)
            mChannel.description = "Awitize Channel"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
        val playerIntent = Intent(this, PlayerService::class.java)
        ContextCompat.startForegroundService(this, playerIntent)
    }

    override fun onTerminate() {
        super.onTerminate()
        val i = Intent(PlayerServiceEvents.DESTROY.name)
        LocalBroadcastManager.getInstance(this).sendBroadcast(i)
    }
}