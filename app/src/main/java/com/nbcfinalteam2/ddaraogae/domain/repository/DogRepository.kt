package com.nbcfinalteam2.ddaraogae.domain.repository

import android.net.Uri
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

interface DogRepository {
    suspend fun getDogList(): List<DogEntity>
    suspend fun getDogById(dogId: String): DogEntity?
    suspend fun insertDog(dogEntity: DogEntity, imageUri: Uri?)
    suspend fun updateDog(dogEntity: DogEntity, imageUri: Uri?)
    suspend fun deleteDog(dogId: String)
}