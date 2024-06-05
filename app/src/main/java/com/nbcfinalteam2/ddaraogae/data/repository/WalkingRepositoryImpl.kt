package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.FirebaseDataSource
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toDto
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WalkingRepository
import java.util.Date

class WalkingRepositoryImpl(
    private val firebaseDateSource: FirebaseDataSource
): WalkingRepository {
    override suspend fun getWalkingListByDogIdAndPeriod(
        dogId: String,
        start: Date,
        end: Date
    ): List<WalkingEntity> {
        return firebaseDateSource.getWalkingListByDogIdAndPeriod(dogId, start, end).map {
            it.second.toEntity(it.first)
        }
    }

    override suspend fun getWalkingById(walkingId: String): WalkingEntity? {
        return firebaseDateSource.getWalkingById(walkingId)?.toEntity(walkingId)
    }

    override suspend fun insertWalkingData(walkingEntity: WalkingEntity) {
        firebaseDateSource.insertWalkingData(
            walkingEntity.toDto()
        )
    }
}