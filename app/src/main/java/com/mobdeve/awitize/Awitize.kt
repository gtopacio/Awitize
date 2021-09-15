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
            val generalChannel = NotificationChannel(GENERAL_CHANNEL_ID, "General Channel", importance)
            generalChannel.description = "Channel use to display all notifications of Awitize"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(generalChannel)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        val i = Intent(PlayerServiceEvents.DESTROY.name)
        LocalBroadcastManager.getInstance(this).sendBroadcast(i)
    }

    companion object{
        const val GENERAL_CHANNEL_ID : String = "Awitize"
    }
}