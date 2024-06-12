package com.nbcfinalteam2.ddaraogae.domain.usecase

import android.net.Uri
import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WalkingRepository
import javax.inject.Inject

class InsertWalkingDataUseCaseImpl @Inject constructor(
    private val walkingRepository: WalkingRepository
) : InsertWalkingDataUseCase {
    override suspend fun invoke(walkingEntity: WalkingEntity, mapImage: Uri?) =
        walkingRepository.insertWalkingData(walkingEntity, mapImage)
}