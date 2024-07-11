package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmListUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository
): GetAlarmListUseCase {
    override fun invoke(): Flow<List<AlarmEntity>> = alarmRepository.getAlarmListFlow()
}