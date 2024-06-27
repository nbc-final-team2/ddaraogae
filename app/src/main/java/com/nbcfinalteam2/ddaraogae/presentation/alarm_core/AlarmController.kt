package com.nbcfinalteam2.ddaraogae.presentation.alarm_core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmConstant.EXTRA_ALARM_ID
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmConstant.EXTRA_ALARM_SET_TIME
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class AlarmController @Inject constructor(
    private val alarmManager: AlarmManager,
    @ApplicationContext private val context: Context
) {

    fun setAlarm(id: Int, setTime: Int) {

        if(checkExactAlarmPermission()) return

        val hour = setTime/60
        val minute = setTime%60

        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if(alarmTime.timeInMillis <= System.currentTimeMillis()) {
            alarmTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.putExtra(EXTRA_ALARM_ID, id)
        alarmIntent.putExtra(EXTRA_ALARM_SET_TIME, setTime)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                alarmTime.timeInMillis,
                pendingIntent
            ),
            pendingIntent
        )

    }

    fun deleteAlarm(id: Int) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.putExtra(EXTRA_ALARM_ID, id)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun checkExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

}