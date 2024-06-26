package com.nbcfinalteam2.ddaraogae.data.repository

import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.nbcfinalteam2.ddaraogae.data.datasource.local.AlarmDataSource
import com.nbcfinalteam2.ddaraogae.data.mapper.AlarmMapper.toPreference
import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDataSource: AlarmDataSource
): AlarmRepository {
    private val gson = Gson()

    override suspend fun insertAlarm(alarmEntity: AlarmEntity) {
        alarmDataSource.insertAlarm(
            stringPreferencesKey(alarmEntity.id.toString()),
            gson.toJson(alarmEntity.toPreference())
        )
    }

    override fun getAlarmList(): Flow<List<AlarmEntity>> {
        return alarmDataSource.getAlarmList().map { preferences ->
            preferences.asMap().values.map { value ->
                gson.fromJson(value as String, AlarmEntity::class.java)
            }
        }
    }

    override suspend fun updateAlarm(alarmEntity: AlarmEntity) {
        alarmDataSource.updateAlarm(
            stringPreferencesKey(alarmEntity.id.toString()),
            gson.toJson(alarmEntity.toPreference())
        )
    }

    override suspend fun deleteAlarm(alarmId: Long) {
        alarmDataSource.deleteAlarm(
            stringPreferencesKey(alarmId.toString())
        )
    }
}