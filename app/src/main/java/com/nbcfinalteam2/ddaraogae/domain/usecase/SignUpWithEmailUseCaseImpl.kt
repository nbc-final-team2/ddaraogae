package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository

class SignUpWithEmailUseCaseImpl(
    private val authRepository: AuthRepository
) : SignUpWithEmailUseCase {
    override suspend fun invoke(emailAuthEntity: EmailAuthEntity): Boolean =
        authRepository.signUpWithEmail(emailAuthEntity)
}