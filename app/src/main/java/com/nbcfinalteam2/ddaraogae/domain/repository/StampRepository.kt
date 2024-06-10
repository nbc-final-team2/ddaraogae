package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity

interface StampRepository {
    suspend fun getStampNumByDogIdAndPeriod(dogId: String, start: Long, end: Long): Int
    suspend fun insertStamp(stampEntity: StampEntity)
}