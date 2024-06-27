package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.StampInfoEntity
import java.util.Date

interface StampRepository {
    suspend fun getStampNumByPeriod(start: Date, end: Date): Int
    suspend fun getStampListByPeriod(start: Date, end: Date): List<StampEntity>
    suspend fun insertStamp(stampEntity: StampEntity)
    suspend fun checkStampCondition(date: Date): List<StampEntity>
    fun getStampInfoList(): List<StampInfoEntity>
}