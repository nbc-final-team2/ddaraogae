package com.nbcfinalteam2.ddaraogae.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmDataSourceImpl @Inject constructor(
    private val alarmPreferencesStore: DataStore<Preferences>
): AlarmDataSource {
    override suspend fun insertAlarm(key: Preferences.Key<String>, alarmPreference: String) {
        alarmPreferencesStore.edit { preferences ->
            preferences[key] = alarmPreference
        }
    }

    override fun getAlarmList(): Flow<Preferences> {
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