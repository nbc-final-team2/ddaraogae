package com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.data.dto.StampDto
import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor(
    private val firebaseFs: FirebaseFirestore,
    private val fbAuth: FirebaseAuth,
    private val fbStorage: FirebaseStorage
): FirebaseDataSource {

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

    override suspend fun insertDog(dogDto: DogDto, imageUri: Uri?) {
        val uid = getUserUid()
        val db = firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_DOGS)
        val newDogDoc = db.document()
        val dogId = newDogDoc.id

        newDogDoc.set(dogDto).await()
        updateDog(dogId, dogDto, imageUri)
    }

    override suspend fun updateDog(dogId: String, dogDto: DogDto, imageUri: Uri?) {
        val uid = getUserUid()
        val db = firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_DOGS).document(dogId)

        val updateDogDto = imageUri?.let { uri ->
            val convertedUrl = convertImageUrl(uri, dogId)
            dogDto.copy(thumbnailUrl = convertedUrl.toString())
        } ?: dogDto

        db.set(updateDogDto).await()
    }

    override suspend fun deleteDog(dogId: String) {
        val storageRef = fbStorage.reference
        val uid = getUserUid()
        val deleteRef = storageRef.child("$PATH_USERDATA/$uid/$PATH_DOGS/$dogId.$STORAGE_FILE_EXTENSION")

        firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_DOGS).document(dogId)
            .delete().await()

        deleteRef.delete()
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

    /**
     * stamp number
     * #0 : UNKNOWN
     * #1 : 1.5km per day
     * #2~4 : consecutive 3/5/7days
     * #5 : 7 walking in a week
     * #6~8 : total distance 10km+/15km+/20km+
     */
    override suspend fun checkStampCondition(dogId: String, date: Date): List<Pair<String, StampDto>> {
        val uid = getUserUid()

        val (mondayStart, sundayEnd) = date.getWeekStartAndEnd()

        val queriedWalkingList = firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_WALKING)
            .whereEqualTo(FIELD_DOG_ID, dogId)
            .whereGreaterThanOrEqualTo(FIELD_START_DATETIME, mondayStart)
            .whereLessThanOrEqualTo(FIELD_START_DATETIME, sundayEnd)
            .orderBy(FIELD_START_DATETIME, Query.Direction.ASCENDING)
            .get().await()
            .map {
                it.toObject(WalkingDto::class.java)
            }

        val queriedStampList = List<MutableList<StampDto>>(9) { mutableListOf() }

        firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_STAMPS)
            .whereGreaterThanOrEqualTo(FIELD_GET_DATETIME, mondayStart)
            .whereLessThanOrEqualTo(FIELD_GET_DATETIME, sundayEnd)
            .get().await()
            .forEach {
                val stamp = it.toObject(StampDto::class.java)
                queriedStampList[stamp.stampNum?:0].add(stamp)
            }

        val getStampList = mutableListOf<StampDto>()
        val resultList = mutableListOf<Pair<String, StampDto>>()

        //#1
        if(queriedStampList[1].none { it.getDateTime?.toLocalDate()?.isEqual(date.toLocalDate()) == true }) {
            if(DataUtil.getTotalDistanceInADay(date, queriedWalkingList) >= 1.5) {
                getStampList.add(
                    StampInfo.STAMP_1.toStampDto(date)
                )
            }
        }

        //#2~4
        var passFlag = true
        for(i in 2..4) {
            if(queriedStampList[i].isEmpty()) {
                passFlag = false
                break
            }
        }
        if(!passFlag) {
            val longestConsecutiveDays = DataUtil.getLongestConsecutiveWalkingDays(queriedWalkingList)
            if(queriedStampList[2].isEmpty() && longestConsecutiveDays>=3) {
                getStampList.add(
                    StampInfo.STAMP_2.toStampDto(date)
                )
            }
            if(queriedStampList[3].isEmpty() && longestConsecutiveDays>=5) {
                getStampList.add(
                    StampInfo.STAMP_3.toStampDto(date)
                )
            }
            if(queriedStampList[4].isEmpty() && longestConsecutiveDays>=7) {
                getStampList.add(
                    StampInfo.STAMP_4.toStampDto(date)
                )
            }
        }

        //#5
        if(queriedStampList[5].isEmpty() && queriedWalkingList.size>=7) {
            getStampList.add(
                StampInfo.STAMP_5.toStampDto(date)
            )
        }

        //#6~8
        passFlag = true
        for(i in 6..8) {
            if(queriedStampList[i].isEmpty()) {
                passFlag = false
                break
            }
        }
        if(!passFlag) {
            val totalDistance = queriedWalkingList.sumOf { it.distance?:0.0 }
            if(queriedStampList[6].isEmpty() && totalDistance>=10) {
                getStampList.add(
                    StampInfo.STAMP_6.toStampDto(date)
                )
            }
            if(queriedStampList[7].isEmpty() && totalDistance>=15) {
                getStampList.add(
                    StampInfo.STAMP_7.toStampDto(date)
                )
            }
            if(queriedStampList[8].isEmpty() && totalDistance>=20) {
                getStampList.add(
                    StampInfo.STAMP_8.toStampDto(date)
                )
            }
        }

        for(stamp in getStampList) {
            val docRef = firebaseFs.collection(PATH_USERDATA).document(uid)
                .collection(PATH_STAMPS)
                .add(stamp).await()

            resultList.add(docRef.id to stamp)
        }

        return resultList
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
            .whereGreaterThanOrEqualTo(FIELD_START_DATETIME, start)
            .whereLessThanOrEqualTo(FIELD_START_DATETIME, end)
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

    private suspend fun convertImageUrl(imageUri: Uri, dogId: String): Uri {
        val storageRef = fbStorage.reference
        val uid = getUserUid()
        val uploadRef = storageRef.child("$PATH_USERDATA/$uid/$PATH_DOGS/$dogId.$STORAGE_FILE_EXTENSION")

        uploadRef.putFile(imageUri).await()

        return uploadRef.downloadUrl.await()
    }

    companion object {
        private const val PATH_USERDATA = "userData"
        private const val PATH_DOGS = "dogs"
        private const val PATH_STAMPS = "stamps"
        private const val PATH_WALKING = "walking"

        private const val FIELD_DOG_ID = "dogId"
        private const val FIELD_GET_DATETIME = "getDateTime"
        private const val FIELD_START_DATETIME = "startDateTime"

        private const val STORAGE_FILE_EXTENSION = "jpg"
    }
}