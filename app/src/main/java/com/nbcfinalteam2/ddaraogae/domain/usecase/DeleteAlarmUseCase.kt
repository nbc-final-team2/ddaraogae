package com.nbcfinalteam2.ddaraogae.domain.usecase

interface DeleteAlarmUseCase {
    suspend operator fun invoke(alarmId: Int)
}