package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): SignInWithGoogleUseCase {
    override suspend fun invoke(idToken: String): Boolean = authRepository.signInWithGoogle(idToken)
}