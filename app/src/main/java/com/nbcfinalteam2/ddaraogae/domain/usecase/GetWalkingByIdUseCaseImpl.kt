package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WalkingRepository

class GetWalkingByIdUseCaseImpl(
    private val walkingRepository: WalkingRepository
) : GetWalkingByIdUseCase {
    override suspend fun invoke(walkingId: String): WalkingEntity? =
        walkingRepository.getWalkingById(walkingId)
}