package com.nbcfinalteam2.ddaraogae.data.datasource.local

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface AlarmDataSource {
    suspend fun insertAlarm(key: Preferences.Key<String>, alarmPreference: String)
    fun getAlarmList(): Flow<Preferences>
    suspend fun updateAlarm(key: Preferences.Key<String>, alarmPreference: String)
    suspend fun deleteAlarm(key: Preferences.Key<String>)
}