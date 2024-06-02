package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import java.util.Date

interface StampRepository {
    fun getStampNumByDogIdAndPeriod(dogId: Long, start: Date, end: Date): Int
    suspend fun insertStamp(stampEntity: StampEntity)
}