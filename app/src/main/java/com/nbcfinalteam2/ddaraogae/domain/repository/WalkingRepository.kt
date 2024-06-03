package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity

interface WalkingRepository {
    suspend fun getWalkingListByDogIdAndPeriod(dogId: String, start: Long, end: Long): List<WalkingEntity>
    suspend fun getWalkingById(walkingId: String): WalkingEntity
    suspend fun insertWalkingData(walkingEntity: WalkingEntity)
}