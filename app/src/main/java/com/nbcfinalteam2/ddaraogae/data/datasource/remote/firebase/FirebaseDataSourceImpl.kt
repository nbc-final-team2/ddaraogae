package com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.data.dto.StampDto
import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirebaseDataSourceImpl(
    private val firebaseFs: FirebaseFirestore
): FirebaseDataSource {

    private val fbAuth = Firebase.auth

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

    override suspend fun getStampNumByDogIdAndPeriod(dogId: String, start: Date, end: Date): Int {
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
        val uid = getUserUid()

        firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_STAMPS)
            .add(stampDto).await()
    }

    override suspend fun getWalkingListByDogIdAndPeriod(
        dogId: String,
        start: Date,
        end: Date
    ): List<Pair<String, WalkingDto>> {
        val uid = getUserUid()

        val queriedList = firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_WALKING)
            .whereEqualTo(FIELD_DOG_ID, dogId)
            .whereGreaterThanOrEqualTo(FIELD_GET_DATETIME, start)
            .whereLessThanOrEqualTo(FIELD_GET_DATETIME, end)
            .get().await()
            .map {
                it.id to it.toObject(WalkingDto::class.java)
            }

        return queriedList
    }

    override suspend fun getWalkingById(walkingId: String): WalkingDto? {
        val uid = getUserUid()

        return firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_WALKING).document(walkingId)
            .get().await()
            .toObject(WalkingDto::class.java)
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