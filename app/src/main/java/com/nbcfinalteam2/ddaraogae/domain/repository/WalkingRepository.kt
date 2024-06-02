package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import java.util.Date

interface WalkingRepository {
    fun getWalkingListByDogIdAndPeriod(dogId: Long, start: Date, end: Date): List<WalkingEntity>
    fun getWalkingById(walkingId: Long): WalkingEntity
    suspend fun insertWalkingData(walkingEntity: WalkingEntity)
}