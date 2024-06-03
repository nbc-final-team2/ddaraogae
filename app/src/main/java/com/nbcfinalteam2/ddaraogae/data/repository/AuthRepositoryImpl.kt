package com.nbcfinalteam2.ddaraogae.data.repository

import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.UserEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl: AuthRepository {
    private val firebaseAuth = Firebase.auth

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
}