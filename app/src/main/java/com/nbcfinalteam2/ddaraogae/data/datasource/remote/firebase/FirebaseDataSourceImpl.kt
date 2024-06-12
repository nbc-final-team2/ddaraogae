package com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.data.dto.StampDto
import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import kotlinx.coroutines.tasks.await
import java.io.File
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
            val convertedUrl = convertImageUrl(uri, dogId, PATH_DOGS)
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

    override suspend fun insertWalkingData(walkingDto: WalkingDto, mapImage: Uri?, context: Context) {
        val uid = getUserUid()

        val db = firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_WALKING)
        val newWalkingDoc = db.document()
        val walkingId = newWalkingDoc.id

        newWalkingDoc.set(walkingDto).await()
        updateWalkingData(walkingId, walkingDto, mapImage, context)
    }

    override suspend fun updateWalkingData(walkingId: String, walkingDto: WalkingDto, mapImage: Uri?, context: Context) {
        val uid = getUserUid()
        val db = firebaseFs.collection(PATH_USERDATA).document(uid)
            .collection(PATH_WALKING).document(walkingId)

        val updateWalkingDto = mapImage?.let { bitmap ->
            val convertedUrl = convertImageUrl(bitmap, walkingId, PATH_WALKING)
            walkingDto.copy(walkingImage = convertedUrl.toString())
        } ?: walkingDto

        db.set(updateWalkingDto).addOnSuccessListener {
            deleteCachedImage(context)
        }
    }

    private fun getUserUid(): String {
        return fbAuth.currentUser?.uid?:throw Exception("UNKNOWN USER")
    }

    private suspend fun convertImageUrl(imageUri: Uri, itemId: String, path: String): Uri {
        val storageRef = fbStorage.reference
        val uid = getUserUid()
        val uploadRef = storageRef.child("$PATH_USERDATA/$uid/$path/$itemId.$STORAGE_FILE_EXTENSION")

        uploadRef.putFile(imageUri).await()

        return uploadRef.downloadUrl.await()
    }

    private fun deleteCachedImage(context: Context) {
        val cacheDir = context.cacheDir
        cacheDir?.let { directory ->
            directory.listFiles()?.forEach { file ->
                file.delete()
            }
        }
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