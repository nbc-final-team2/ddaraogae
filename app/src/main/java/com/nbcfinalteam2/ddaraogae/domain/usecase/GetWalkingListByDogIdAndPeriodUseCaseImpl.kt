package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WalkingRepository
import java.util.Date

class GetWalkingListByDogIdAndPeriodUseCaseImpl(
    private val walkingRepository: WalkingRepository
): GetWalkingListByDogIdAndPeriodUseCase {
    override suspend fun invoke(dogId: String, start: Date, end: Date): List<WalkingEntity> =
        walkingRepository.getWalkingListByDogIdAndPeriod(dogId, start, end)
}