package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.FirebaseDataSource
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toDto
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.StampRepository
import java.util.Date

class StampRepositoryImpl(
    private val firebaseDateSource: FirebaseDataSource
): StampRepository {
    override suspend fun getStampNumByDogIdAndPeriod(dogId: String, start: Date, end: Date): Int {
        return firebaseDateSource.getStampNumByDogIdAndPeriod(dogId, start, end)
    }

    override suspend fun insertStamp(stampEntity: StampEntity) {
        firebaseDateSource.insertStamp(stampEntity.toDto())
    }
}