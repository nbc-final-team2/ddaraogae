package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity

interface InsertWalkingDataUseCase {
    suspend operator fun invoke(walkingEntity: WalkingEntity, mapImage: ByteArray)
}