package com.nbcfinalteam2.ddaraogae.presentation.alarm_core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AlarmRepository
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmConstant.EXTRA_ALARM_ID
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmConstant.EXTRA_ALARM_SET_TIME
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

class AlarmController @Inject constructor(
    private val alarmManager: AlarmManager,
    private val context: Context,
    private val alarmRepository: AlarmRepository,
    dispatcher: CoroutineDispatcher
) {

    private val scope = CoroutineScope(dispatcher)

    fun createAlarm(setTime: Int, uid: String) {
        scope.launch {
            runCatching {
                val id = alarmRepository.insertAlarm(
                    AlarmEntity(-1, setTime, uid)
                )

                setAlarm(id, setTime)
            }
        }
    }

    fun setAlarm(id: Int, setTime: Int) {
        if(!checkExactAlarmPermission()) return
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
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                alarmTime.timeInMillis,
                pendingIntent
            ),
            pendingIntent
        )

    }

    fun setAllAlarms(uid: String) {
        scope.launch {
            runCatching {
                alarmRepository.getAlarmList(uid).onEach {
                    setAlarm(it.id, it.setTime)
                }
            }
        }

    }

    fun getAlarmListFlow(uid: String): Flow<List<AlarmEntity>> {
        return alarmRepository.getAlarmListFlow(uid)
    }

    fun updateAlarm(id: Int, setTime: Int, uid: String) {
        scope.launch {
            runCatching {
                alarmRepository.updateAlarm(
                    AlarmEntity(id, setTime, uid)
                )

                setAlarm(id, setTime)
            }
        }
    }

    fun deleteAlarm(id: Int) {
        scope.launch {
            runCatching {
                alarmRepository.deleteAlarm(id)

                unsetAlarm(id)
            }
        }
    }

    fun deleteAllAlarm(uid: String) {
        scope.launch {
            runCatching {
                alarmRepository.getAlarmList(uid).onEach {
                    alarmRepository.deleteAlarm(it.id)
                }
                unsetAllAlarms(uid)
            }
        }
    }

    private fun unsetAlarm(id: Int) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.putExtra(EXTRA_ALARM_ID, id)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            alarmIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

        fun unsetAllAlarms(uid: String) {
        scope.launch {
            runCatching {
                alarmRepository.getAlarmList(uid).onEach {
                    unsetAlarm(it.id)
                }
            }
        }
    }

    private fun checkExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

}