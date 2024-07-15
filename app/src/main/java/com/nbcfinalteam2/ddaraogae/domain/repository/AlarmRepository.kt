package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun insertAlarm(alarmEntity: AlarmEntity): Int
    suspend fun getAlarmList(uid: String): List<AlarmEntity>
    fun getAlarmListFlow(uid: String): Flow<List<AlarmEntity>>
    suspend fun updateAlarm(alarmEntity: AlarmEntity)
    suspend fun deleteAlarm(alarmId: Int)
}