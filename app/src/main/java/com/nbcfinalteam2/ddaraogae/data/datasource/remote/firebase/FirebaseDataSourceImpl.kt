package com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.data.dto.SpotDto
import com.nbcfinalteam2.ddaraogae.data.dto.StampDto
import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import kotlinx.coroutines.tasks.await

class FirebaseDataSourceImpl(
    private val firebaseRef: DatabaseReference
): FirebaseDataSource {
    override suspend fun getDogList(): List<DogDto> {
        val snapshot = firebaseRef.child("DOGS").get().await()
        val dogList = mutableListOf<DogDto>()

        for (childSnapshot in snapshot.children) {
            val dog = childSnapshot.getValue<DogDto>()
            dog?.let {
                dogList.add(it)
            }
        }

        return dogList
    }

    override suspend fun getDogById(dogId: String): DogDto? {
        return firebaseRef.child("DOGS").child(dogId).get().await().getValue<DogDto>()
    }

    override suspend fun insertDog(dogDto: DogDto) {
        val key = firebaseRef.child("DOGS").push().key

        if(key != null) {
            val newData = dogDto.copy(id = key)
            firebaseRef.child("DOGS").child(key).setValue(newData)
        }
    }

    override suspend fun updateDog(dogDto: DogDto) {
        val key = dogDto.id

        if(key != null) {
            firebaseRef.child("DOGS").child(key).setValue(dogDto)
        }
    }

    override suspend fun deleteDog(dogId: String) {
        firebaseRef.child("DOGS").child(dogId).removeValue()
    }

    override suspend fun getSpotList(): List<SpotDto> {
        val snapshot = firebaseRef.child("SPOTS").get().await()
        val spotList = mutableListOf<SpotDto>()

        for (childSnapshot in snapshot.children) {
            val spot = childSnapshot.getValue<SpotDto>()
            spot?.let {
                spotList.add(it)
            }
        }

        return spotList
    }

    override suspend fun getStampNumByDogIdAndPeriod(dogId: String, start: Long, end: Long): Int {
        val snapshot = firebaseRef.child("STAMPS")
            .orderByChild("dogId").equalTo(dogId)
            .orderByChild("getDateTime").startAt(start.toDouble()).endAt(end.toDouble())
            .get()
            .await()

        return snapshot.childrenCount.toInt()
    }

    override suspend fun insertStamp(stampDto: StampDto) {
        val key = firebaseRef.child("STAMPS").push().key

        if(key != null) {
            val newData = stampDto.copy(id = key)
            firebaseRef.child("STAMPS").child(key).setValue(newData)
        }
    }

    override suspend fun getWalkingListByDogIdAndPeriod(
        dogId: String,
        start: Long,
        end: Long
    ): List<WalkingDto> {
        //todo 복합쿼리 지원 안하는 문제
        val snapshot = firebaseRef.child("WALKING")
            .orderByChild("startDateTime").startAt(start.toDouble()).endAt(end.toDouble())
            .get()
            .await()

        return snapshot.children.map {
            it.getValue<WalkingDto>()!!
        }
    }

    override suspend fun getWalkingById(walkingId: String): WalkingDto? {
        return firebaseRef.child("WALKING").child(walkingId).get().await().getValue<WalkingDto>()
    }

    override suspend fun insertWalkingData(walkingDto: WalkingDto) {
        val key = firebaseRef.child("WALKING").push().key

        if(key != null) {
            val newData = walkingDto.copy(id = key)
            firebaseRef.child("WALKING").child(key).setValue(newData)
        }
    }

}