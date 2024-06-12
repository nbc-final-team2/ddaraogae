package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.FirebaseDataSource
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toDto
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.StampRepository
import java.util.Date
import javax.inject.Inject

class StampRepositoryImpl @Inject constructor(
    private val firebaseDateSource: FirebaseDataSource
): StampRepository {
    override suspend fun getStampNumByDogIdAndPeriod(dogId: String, start: Date, end: Date): Int {
        return firebaseDateSource.getStampNumByDogIdAndPeriod(dogId, start, end)
    }

    override suspend fun insertStamp(stampEntity: StampEntity) {
        firebaseDateSource.insertStamp(stampEntity.toDto())
    }

    override suspend fun checkStampCondition(dogId: String, date: Date): List<StampEntity> {
        return firebaseDateSource.checkStampCondition(dogId, date).map {
            it.second.toEntity(it.first)
        }
    }
}