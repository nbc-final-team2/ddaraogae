package com.nbcfinalteam2.ddaraogae.presentation.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LocationService: Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}