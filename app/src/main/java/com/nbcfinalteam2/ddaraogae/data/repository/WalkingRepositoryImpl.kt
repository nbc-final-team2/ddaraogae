package com.nbcfinalteam2.ddaraogae.data.repository

import android.net.Uri
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.FirebaseDataSource
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toDto
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WalkingRepository
import java.util.Date
import javax.inject.Inject

class WalkingRepositoryImpl @Inject constructor(
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

    override suspend fun insertWalkingData(walkingEntity: WalkingEntity, mapImage: Uri?) {
        firebaseDateSource.insertWalkingData(walkingEntity.toDto(), mapImage)
    }
}