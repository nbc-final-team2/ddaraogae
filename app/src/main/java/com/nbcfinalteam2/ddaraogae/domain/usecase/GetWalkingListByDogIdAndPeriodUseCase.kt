package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WalkingRepository
import java.util.Date

interface GetWalkingListByDogIdAndPeriodUseCase{
    suspend operator fun invoke(dogId: String, start: Date, end: Date): List<WalkingEntity>
}