package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AlarmRepository
import javax.inject.Inject

class UpdateAlarmUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository
): UpdateAlarmUseCase {
    override suspend fun invoke(alarmEntity: AlarmEntity) = alarmRepository.updateAlarm(alarmEntity)
}