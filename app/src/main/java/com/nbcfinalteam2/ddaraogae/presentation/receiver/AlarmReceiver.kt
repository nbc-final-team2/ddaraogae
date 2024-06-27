package com.nbcfinalteam2.ddaraogae.presentation.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmReceiver @Inject constructor(
    @ApplicationContext private val context: Context
): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        createNotificationChannel()

    }

    private fun createNotificationChannel() {
        val alarmChannel = NotificationChannel(
            CHANNEL_ID,
            "Alarm Channel",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager = getSystemService(context, NotificationManager::class.java)
        manager?.createNotificationChannel(alarmChannel)
    }

    companion object {
        private const val CHANNEL_ID = "AlarmChannel"
        private const val NOTIFICATION_ID = 1
    }

}