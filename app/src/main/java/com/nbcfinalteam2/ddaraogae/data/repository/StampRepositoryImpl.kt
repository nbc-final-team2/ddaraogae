package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.FirebaseDataSource
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.StampInfo
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toDto
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.StampInfoEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.StampRepository
import java.util.Date
import javax.inject.Inject

class StampRepositoryImpl @Inject constructor(
    private val firebaseDateSource: FirebaseDataSource
): StampRepository {
    override suspend fun getStampNumByPeriod(start: Date, end: Date): Int {
        return firebaseDateSource.getStampNumByPeriod(start, end)
    }

    override suspend fun getStampListByPeriod(start: Date, end: Date): List<StampEntity> {
        return firebaseDateSource.getStampListByPeriod(start, end).map {
            it.second.toEntity(it.first)
        }
    }

    override suspend fun insertStamp(stampEntity: StampEntity) {
        firebaseDateSource.insertStamp(stampEntity.toDto())
    }

    override suspend fun checkStampCondition(date: Date): List<StampEntity> {
        return firebaseDateSource.checkStampCondition(date).map {
            it.second.toEntity(it.first)
        }
    }

    override fun getStampInfoList(): List<StampInfoEntity> {
        return StampInfo.stampInfoList.map { it.toEntity() }
    }
}