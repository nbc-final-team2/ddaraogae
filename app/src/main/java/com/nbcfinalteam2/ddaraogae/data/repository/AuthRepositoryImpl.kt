package com.nbcfinalteam2.ddaraogae.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.FirebaseDataSourceImpl
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
        return result.user != null
    }

    override suspend fun signUpWithEmail(emailAuthEntity: EmailAuthEntity): Boolean {
        val result = firebaseAuth.createUserWithEmailAndPassword(emailAuthEntity.email, emailAuthEntity.password).await()
        return result.user != null
    }

    override suspend fun signInWithGoogle(idToken: String): Boolean {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(firebaseCredential).await()
        return result.user != null
    }

    override suspend fun deleteAccount() {
        val uid = firebaseAuth.currentUser?.uid ?: throw Exception("USER NOT EXIST")

        firebaseFs.collection(PATH_USERDATA).document(uid).delete().await()
        val deleteRef = fbStorage.reference.child("$PATH_USERDATA/$uid")
        deleteRef.listAll().await().prefixes.forEach { pref ->
            pref.listAll().await().items.forEach { item ->
                item.delete().await()
            }
            pref.delete().await()
        }
        deleteRef.delete()

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

    companion object {
        private const val PATH_USERDATA = "userData"
    }
}