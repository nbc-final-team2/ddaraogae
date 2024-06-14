package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.StampRepository
import java.util.Date
import javax.inject.Inject

class GetStampNumByDogIdAndPeriodUseCaseImpl @Inject constructor(
    private val stampRepository: StampRepository
) : GetStampNumByDogIdAndPeriodUseCase {
    override suspend fun invoke(dogId: String, start: Date, end: Date): Int =
        stampRepository.getStampNumByDogIdAndPeriod(dogId, start, end)
}