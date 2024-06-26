package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.AlarmRepository
import javax.inject.Inject

class DeleteAlarmUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository
): DeleteAlarmUseCase {
    override suspend fun invoke(alarmId: Long) = alarmRepository.deleteAlarm(alarmId)
}