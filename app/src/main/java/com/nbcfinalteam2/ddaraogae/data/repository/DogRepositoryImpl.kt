package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.FirebaseDataSource
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toDto
import com.nbcfinalteam2.ddaraogae.data.mapper.FirebaseMapper.toEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.DogRepository
import javax.inject.Inject

class DogRepositoryImpl @Inject constructor(
    private val firebaseDateSource: FirebaseDataSource
): DogRepository {
    override suspend fun getDogList(): List<DogEntity> {
        return firebaseDateSource.getDogList().map {
            it.second.toEntity(it.first)
        }
    }

    override suspend fun getDogById(dogId: String): DogEntity? {
        return firebaseDateSource.getDogById(dogId)?.toEntity(dogId)
    }

    override suspend fun insertDog(dogEntity: DogEntity) {
        firebaseDateSource.insertDog(dogEntity.toDto())
    }

    override suspend fun updateDog(dogEntity: DogEntity) {
        firebaseDateSource.updateDog(dogEntity.id.toString(), dogEntity.toDto())
    }

    override suspend fun deleteDog(dogId: String) {
        firebaseDateSource.deleteDog(dogId)
    }
}