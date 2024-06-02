package com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase

import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

interface FirebaseDataSource {
    fun getDogList(): List<DogDto>
    fun getDogById(dogId: String): DogDto
    suspend fun insertDog(dogDto: DogDto)
    suspend fun updateDog(dogDto: DogDto)
    suspend fun deleteDog(dogDto: DogDto)
}