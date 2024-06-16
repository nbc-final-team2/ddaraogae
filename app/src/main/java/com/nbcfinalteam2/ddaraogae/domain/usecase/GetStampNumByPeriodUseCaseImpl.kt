package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.StampRepository
import java.util.Date
import javax.inject.Inject

class GetStampNumByPeriodUseCaseImpl @Inject constructor(
    private val stampRepository: StampRepository
) : GetStampNumByPeriodUseCase {
    override suspend fun invoke(start: Date, end: Date): Int =
        stampRepository.getStampNumByPeriod(start, end)
}