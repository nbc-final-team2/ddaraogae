package com.nbcfinalteam2.ddaraogae.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.UserEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFs: FirebaseFirestore,
    private val fbStorage: FirebaseStorage
): AuthRepository {

    override suspend fun signInWithEmail(emailAuthEntity: EmailAuthEntity): Boolean {
        val result = firebaseAuth.signInWithEmailAndPassword(emailAuthEntity.email, emailAuthEntity.password).await()
        if(result.user!=null) {
            firebaseAuth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(PROVIDER_EMAIL)
                    .build()
            )
        }
        return result.user != null
    }

    override suspend fun signUpWithEmail(emailAuthEntity: EmailAuthEntity): Boolean {
        val result = firebaseAuth.createUserWithEmailAndPassword(emailAuthEntity.email, emailAuthEntity.password).await()
        return result.user != null
    }

    override suspend fun signInWithGoogle(idToken: String): Boolean {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(firebaseCredential).await()
        if(result.user!=null) {
            firebaseAuth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(PROVIDER_GOOGLE)
                    .build()
            )
        }
        return result.user != null
    }

    override suspend fun deleteAccount(credential: String) {
        val uid = firebaseAuth.currentUser?.uid ?: throw Exception("USER NOT EXIST")

        firebaseAuth.currentUser?.let {
            if(it.displayName == PROVIDER_GOOGLE) {
                it.reauthenticate(
                    GoogleAuthProvider.getCredential(credential, null)
                ).await()
            } else {
                it.reauthenticate(
                    EmailAuthProvider.getCredential(it.email.toString(), credential)
                ).await()
            }
        }

        firebaseFs.collection(PATH_USERDATA).document(uid).collection(PATH_DOGS).get().await().documents.forEach {
            it.reference.delete().await()
        }
        firebaseFs.collection(PATH_USERDATA).document(uid).collection(PATH_STAMPS).get().await().documents.forEach {
            it.reference.delete().await()
        }
        firebaseFs.collection(PATH_USERDATA).document(uid).collection(PATH_WALKING).get().await().documents.forEach {
            it.reference.delete().await()
        }
        firebaseFs.collection(PATH_WALKING_DOGS).document(uid).collection(PATH_WALKING_DOGS).get().await().documents.forEach {
            it.reference.delete().await()
        }

        val deleteDogRef = fbStorage.reference.child("$PATH_USERDATA/$uid/$PATH_DOGS")
        val deleteWalkingRef = fbStorage.reference.child("$PATH_USERDATA/$uid/$PATH_WALKING")

        deleteDogRef.listAll().await().items.forEach { it.delete().await() }
        deleteWalkingRef.listAll().await().items.forEach { it.delete().await() }

        firebaseAuth.currentUser?.delete()?.await()
    }

    override suspend fun sendVerificationEmail() {
        firebaseAuth.currentUser?.sendEmailVerification()?.await()
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): UserEntity? {
        return firebaseAuth.currentUser?.let {
            UserEntity(
                uid = it.uid
            )
        }
    }

    override suspend fun isCurrentUserEmailVerified(): Boolean {
        firebaseAuth.currentUser?.reload()?.await()
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

    override fun isGoogleUser(): Boolean {
        return firebaseAuth.currentUser?.displayName == PROVIDER_GOOGLE
    }

    companion object {
        private const val PATH_USERDATA = "userData"
        private const val PATH_DOGS = "dogs"
        private const val PATH_WALKING = "walking"
        private const val PATH_STAMPS = "stamps"
        private const val PATH_WALKING_DOGS = "walking_dogs"

        private const val PROVIDER_GOOGLE = "google_user"
        private const val PROVIDER_EMAIL = "email_user"
    }
}