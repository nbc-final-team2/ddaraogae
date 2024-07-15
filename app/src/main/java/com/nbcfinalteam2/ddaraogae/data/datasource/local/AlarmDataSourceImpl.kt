package com.nbcfinalteam2.ddaraogae.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nbcfinalteam2.ddaraogae.data.mapper.AlarmMapper
import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import javax.inject.Inject
import kotlin.random.Random

class AlarmDataSourceImpl @Inject constructor(
    private val alarmPreferencesStore: DataStore<Preferences>
): AlarmDataSource {
    override suspend fun insertAlarm(alarmEntity: AlarmEntity): Int {
        var key = Random.nextInt(100000)
        alarmPreferencesStore.edit { preferences ->
            while(preferences.contains(stringPreferencesKey(key.toString()))) {
                key = Random.nextInt(100000)
            }
            preferences[stringPreferencesKey(key.toString())] = AlarmMapper.entityToJson(alarmEntity.copy(id = key))
        }

        return key
    }

    override suspend fun getAlarmList(): Preferences {
        return alarmPreferencesStore.data.first()
    }

    override fun getAlarmListFlow(): Flow<Preferences> {
        return alarmPreferencesStore.data
    }

    override suspend fun updateAlarm(key: Preferences.Key<String>, alarmPreference: String) {
        alarmPreferencesStore.edit { preferences ->
            preferences[key] = alarmPreference
        }
    }

    override suspend fun deleteAlarm(key: Preferences.Key<String>) {
        alarmPreferencesStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}