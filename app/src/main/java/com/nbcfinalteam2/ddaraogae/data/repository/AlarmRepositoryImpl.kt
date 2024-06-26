package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.local.AlarmDataSource
import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDataSource: AlarmDataSource
): AlarmRepository {
    override suspend fun insertAlarm(alarmEntity: AlarmEntity) {
        TODO("Not yet implemented")
    }

    override fun getAlarmList(): Flow<List<AlarmEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAlarm(alarmEntity: AlarmEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarmId: Long) {
        TODO("Not yet implemented")
    }
}