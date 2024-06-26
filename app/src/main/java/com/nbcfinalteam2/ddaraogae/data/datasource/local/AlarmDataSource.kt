package com.nbcfinalteam2.ddaraogae.data.datasource.local

import androidx.datastore.preferences.core.Preferences
import com.nbcfinalteam2.ddaraogae.data.model.AlarmPreferences
import kotlinx.coroutines.flow.Flow

interface AlarmDataSource {
    suspend fun insertAlarm(key: Preferences.Key<Long>, alarmPreferences: AlarmPreferences)
    fun getAlarmList(): Flow<Preferences>
    suspend fun updateAlarm(key: Preferences.Key<Long>, alarmPreferences: AlarmPreferences)
    suspend fun deleteAlarm(key: Preferences.Key<Long>)
}