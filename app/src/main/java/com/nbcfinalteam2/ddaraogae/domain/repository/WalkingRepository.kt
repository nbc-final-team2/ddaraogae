package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import java.util.Date

interface WalkingRepository {
    suspend fun getWalkingListByDogIdAndPeriod(dogId: String, start: Date, end: Date): List<WalkingEntity>
    suspend fun getWalkingById(walkingId: String): WalkingEntity?
    suspend fun insertWalkingData(walkingEntity: WalkingEntity, dogIdList: List<String>, mapImage: ByteArray?)
}