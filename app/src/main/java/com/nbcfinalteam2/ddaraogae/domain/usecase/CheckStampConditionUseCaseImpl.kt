package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.StampRepository
import java.util.Date
import javax.inject.Inject

class CheckStampConditionUseCaseImpl @Inject constructor(
    private val stampRepository: StampRepository
): CheckStampConditionUseCase {
    override suspend fun invoke(dogId: String, date: Date) = stampRepository.checkStampCondition(dogId, date)
}