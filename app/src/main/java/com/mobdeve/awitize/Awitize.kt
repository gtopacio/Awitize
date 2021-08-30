package com.mobdeve.awitize

import android.app.Application
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mobdeve.awitize.enums.PlayerServiceEvents
import com.mobdeve.awitize.service.PlayerService

class Awitize : Application() {

    override fun onCreate() {
        super.onCreate()
        val playerIntent = Intent(this, PlayerService::class.java)
        startService(playerIntent)
    }

    override fun onTerminate() {
        super.onTerminate()
        val i = Intent(PlayerServiceEvents.DESTROY.name)
        LocalBroadcastManager.getInstance(this).sendBroadcast(i)
    }
}