package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

interface DogRepository {
    fun getDogList(): List<DogEntity>
    fun getDogById(dogId: String): DogEntity
    suspend fun insertDog(dogEntity: DogEntity)
    suspend fun updateDog(dogEntity: DogEntity)
    suspend fun deleteDog(dogId: String)
}