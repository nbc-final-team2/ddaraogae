package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun insertAlarm(alarmEntity: AlarmEntity)
    fun getAlarmList(): Flow<List<AlarmEntity>>
    suspend fun updateAlarm(alarmEntity: AlarmEntity)
    suspend fun deleteAlarm(alarmId: Int)
}