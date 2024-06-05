package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity

interface GetWalkingByIdUseCase {
    suspend operator fun invoke(walkingId: String): WalkingEntity?
}