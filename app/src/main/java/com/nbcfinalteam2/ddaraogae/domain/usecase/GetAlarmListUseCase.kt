package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface GetAlarmListUseCase {
    operator fun invoke(): Flow<List<AlarmEntity>>
}