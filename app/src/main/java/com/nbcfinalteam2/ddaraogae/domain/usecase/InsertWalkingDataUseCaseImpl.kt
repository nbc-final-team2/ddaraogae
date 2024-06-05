package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WalkingRepository

class InsertWalkingDataUseCaseImpl(
    private val walkingRepository: WalkingRepository
) : InsertWalkingDataUseCase {
    override suspend fun invoke(walkingEntity: WalkingEntity) =
        walkingRepository.insertWalkingData(walkingEntity)
}