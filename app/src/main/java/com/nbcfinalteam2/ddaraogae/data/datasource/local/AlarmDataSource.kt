package com.nbcfinalteam2.ddaraogae.data.datasource.local

import androidx.datastore.preferences.core.Preferences
import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlarmDataSource {
    suspend fun insertAlarm(alarmEntity: AlarmEntity)
    fun getAlarmList(): Flow<Preferences>
    suspend fun updateAlarm(key: Preferences.Key<String>, alarmPreference: String)
    suspend fun deleteAlarm(key: Preferences.Key<String>)
}