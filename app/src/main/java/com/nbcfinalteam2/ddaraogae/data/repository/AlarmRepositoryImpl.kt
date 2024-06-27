package com.nbcfinalteam2.ddaraogae.data.repository

import androidx.datastore.preferences.core.stringPreferencesKey
import com.nbcfinalteam2.ddaraogae.data.datasource.local.AlarmDataSource
import com.nbcfinalteam2.ddaraogae.data.mapper.AlarmMapper
import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDataSource: AlarmDataSource
): AlarmRepository {

    override suspend fun insertAlarm(alarmEntity: AlarmEntity) {
        alarmDataSource.insertAlarm(
            alarmEntity
        )
    }

    override fun getAlarmList(): Flow<List<AlarmEntity>> {
        return alarmDataSource.getAlarmList().map { preferences ->
            preferences.asMap().values.map { value ->
                AlarmMapper.jsonToEntity(value as String)
            }
        }
    }

    override suspend fun updateAlarm(alarmEntity: AlarmEntity) {
        alarmDataSource.updateAlarm(
            stringPreferencesKey(alarmEntity.id.toString()),
            AlarmMapper.entityToJson(alarmEntity)
        )
    }

    override suspend fun deleteAlarm(alarmId: Int) {
        alarmDataSource.deleteAlarm(
            stringPreferencesKey(alarmId.toString())
        )
    }
}