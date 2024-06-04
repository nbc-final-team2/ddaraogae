package com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase

import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.data.dto.SpotDto
import com.nbcfinalteam2.ddaraogae.data.dto.StampDto
import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity

interface FirebaseDataSource {

    //dog
    suspend fun getDogList(): List<Pair<String, DogDto>>
    suspend fun getDogById(dogId: String): DogDto?
    suspend fun insertDog(dogDto: DogDto)
    suspend fun updateDog(dogId: String, dogDto: DogDto)
    suspend fun deleteDog(dogId: String)

    //spot
    suspend fun getSpotList(): List<SpotDto>

    //stamp
    suspend fun getStampNumByDogIdAndPeriod(dogId: String, start: Long, end: Long): Int
    suspend fun insertStamp(stampDto: StampDto)

    //walking
    suspend fun getWalkingListByDogIdAndPeriod(dogId: String, start: Long, end: Long): List<WalkingDto>
    suspend fun getWalkingById(walkingId: String): WalkingDto?
    suspend fun insertWalkingData(walkingDto: WalkingDto)

}