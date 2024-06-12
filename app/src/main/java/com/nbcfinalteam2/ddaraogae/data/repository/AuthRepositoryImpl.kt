package com.nbcfinalteam2.ddaraogae.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.UserEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
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
}