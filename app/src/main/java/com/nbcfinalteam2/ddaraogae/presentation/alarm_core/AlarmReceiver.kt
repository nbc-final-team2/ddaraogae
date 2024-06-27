package com.nbcfinalteam2.ddaraogae.presentation.alarm_core

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.repository.AlarmRepository
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmConstant.CHANNEL_ID
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmConstant.EXTRA_ALARM_ID
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmConstant.EXTRA_ALARM_SET_TIME
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmConstant.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject lateinit var alarmRepository: AlarmRepository
    @Inject lateinit var alarmController: AlarmController

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BroadCast Receiver", "onReceive()")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                alarmRepository.getAlarmList().single().onEach {
                    alarmController.setAlarm(it.id, it.setTime)
                }
            }
        } else if (intent.action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED) {
            CoroutineScope(Dispatchers.IO).launch {
                alarmRepository.getAlarmList().single().onEach {
                    alarmController.setAlarm(it.id, it.setTime)
                }
            }
        } else {
            Log.d("BroadCast Receiver", "정상 캐치")
            val manager = getSystemService(context, NotificationManager::class.java)
            createNotificationChannel(notificationManager = manager)
            postNotification(context = context, notificationManager = manager)

            val id = intent.getIntExtra(EXTRA_ALARM_ID, -1)
            val setTime = intent.getIntExtra(EXTRA_ALARM_SET_TIME, -1)
            if(id!=-1 && setTime!=-1) {
                alarmController.setAlarm(id, setTime)
            }
        }

    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager?
    ) {
        val alarmChannel = NotificationChannel(
            CHANNEL_ID,
            "Alarm Channel",
            NotificationManager.IMPORTANCE_HIGH
        )


        notificationManager?.createNotificationChannel(alarmChannel)
    }

    private fun postNotification(
        context: Context,
        notificationManager: NotificationManager?
    ) {
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.app_name))
            setContentText(context.getString(R.string.alarm_notification_content))
            setSmallIcon(R.drawable.ic_walk)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            priority = NotificationCompat.PRIORITY_MAX
            setAutoCancel(true)
        }

        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

}