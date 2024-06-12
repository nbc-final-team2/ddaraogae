package com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase

import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.data.dto.StampDto
import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import java.util.Date

interface FirebaseDataSource {

    //dog
    suspend fun getDogList(): List<Pair<String, DogDto>>
    suspend fun getDogById(dogId: String): DogDto?
    suspend fun insertDog(dogDto: DogDto)
    suspend fun updateDog(dogId: String, dogDto: DogDto)
    suspend fun deleteDog(dogId: String)

    //stamp
    suspend fun getStampNumByDogIdAndPeriod(dogId: String, start: Date, end: Date): Int
    suspend fun insertStamp(stampDto: StampDto)
    suspend fun checkStampCondition(dogId: String, date: Date): List<StampDto>

    //walking
    suspend fun getWalkingListByDogIdAndPeriod(dogId: String, start: Date, end: Date): List<Pair<String, WalkingDto>>
    suspend fun getWalkingById(walkingId: String): WalkingDto?
    suspend fun insertWalkingData(walkingDto: WalkingDto)

}