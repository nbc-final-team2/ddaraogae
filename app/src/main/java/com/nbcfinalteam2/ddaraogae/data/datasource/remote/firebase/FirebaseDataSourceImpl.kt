package com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase

import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.data.dto.SpotDto
import com.nbcfinalteam2.ddaraogae.data.dto.StampDto
import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import kotlinx.coroutines.tasks.await

class FirebaseDataSourceImpl(
    private val firebaseRef: DatabaseReference,
    private val firebaseFs: FirebaseFirestore
): FirebaseDataSource {

    val fbAuth = Firebase.auth

    override suspend fun getDogList(): List<Pair<String, DogDto>> {
        val uid = getUserUid()

        return firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_DOGS)
            .get().await()
            .map {
                it.id to it.toObject(DogDto::class.java)
            }
    }

    override suspend fun getDogById(dogId: String): DogDto? {
        val uid = getUserUid()

        return firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_DOGS).document(dogId)
            .get().await()
            .toObject(DogDto::class.java)
    }

    override suspend fun insertDog(dogDto: DogDto) {
        val uid = getUserUid()

        firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_DOGS)
            .add(dogDto).await()
    }

    override suspend fun updateDog(dogId: String, dogDto: DogDto) {
        val uid = getUserUid()

        firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_DOGS).document(dogId)
            .set(dogDto).await()
    }

    override suspend fun deleteDog(dogId: String) {
        val uid = getUserUid()

        firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_DOGS).document(dogId)
            .delete().await()
    }

    override suspend fun getSpotList(): List<SpotDto> {
        return emptyList()
    }

    override suspend fun getStampNumByDogIdAndPeriod(dogId: String, start: Long, end: Long): Int {
        val uid = getUserUid()

        val queriedList = firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_STAMPS)
            .whereEqualTo(FIELD_DOG_ID, dogId)
            .whereGreaterThanOrEqualTo(FIELD_GET_DATETIME, start)
            .whereLessThanOrEqualTo(FIELD_GET_DATETIME, end)
            .get().await()

        return queriedList.size()
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
        val uid = getUserUid()

        firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_WALKING).add(walkingDto)
    }

    private fun getUserUid(): String {
        return fbAuth.currentUser?.uid?:throw Exception("UNKNOWN USER")
    }

    companion object {
        private const val PATH_USERDATA = "userData"
        private const val PATH_DOGS = "dogs"
        private const val PATH_STAMPS = "stamps"
        private const val PATH_WALKING = "walking"

        private const val FIELD_DOG_ID = "dogId"
        private const val FIELD_GET_DATETIME = "getDateTime"
    }
}