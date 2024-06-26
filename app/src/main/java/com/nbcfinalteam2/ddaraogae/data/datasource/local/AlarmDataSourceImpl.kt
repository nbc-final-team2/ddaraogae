package com.nbcfinalteam2.ddaraogae.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.nbcfinalteam2.ddaraogae.data.model.AlarmPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmDataSourceImpl @Inject constructor(
    private val alarmPreferencesStore: DataStore<Preferences>
): AlarmDataSource {
    override suspend fun insertAlarm(key: Preferences.Key<Long>, alarmPreferences: AlarmPreferences) {
        alarmPreferencesStore.edit {

        }
    }

    override fun getAlarmList(): Flow<Preferences> {
        return alarmPreferencesStore.data
    }

    override suspend fun updateAlarm(key: Preferences.Key<Long>, alarmPreferences: AlarmPreferences) {
        alarmPreferencesStore.edit {

        }
    }

    override suspend fun deleteAlarm(key: Preferences.Key<Long>) {
        alarmPreferencesStore.edit {

        }
    }
}