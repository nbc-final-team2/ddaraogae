package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity

interface InsertAlarmUseCase {
    suspend operator fun invoke(alarmEntity: AlarmEntity)
}