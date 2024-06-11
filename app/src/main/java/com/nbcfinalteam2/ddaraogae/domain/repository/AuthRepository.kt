package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.UserEntity

interface AuthRepository {
    suspend fun signInWithEmail(emailAuthEntity: EmailAuthEntity): Boolean
    suspend fun signUpWithEmail(emailAuthEntity: EmailAuthEntity): Boolean
    suspend fun signInWithGoogle(idToken: String): Boolean
    suspend fun deleteAccount()
    fun signOut()
    fun getCurrentUser(): UserEntity?
}