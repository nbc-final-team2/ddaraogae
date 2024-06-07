package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : SignInWithEmailUseCase {
    override suspend fun invoke(emailAuthEntity: EmailAuthEntity): Boolean =
        authRepository.signInWithEmail(emailAuthEntity)
}