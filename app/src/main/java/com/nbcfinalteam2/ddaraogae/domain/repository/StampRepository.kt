package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import java.util.Date

interface StampRepository {
    suspend fun getStampNumByPeriod(start: Date, end: Date): Int
    suspend fun insertStamp(stampEntity: StampEntity)
    suspend fun checkStampCondition(dogId: String, date: Date): List<StampEntity>
}