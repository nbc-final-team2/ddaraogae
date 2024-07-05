package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : SignUpWithEmailUseCase {
    override suspend fun invoke(emailAuthEntity: EmailAuthEntity): Boolean =
        authRepository.signUpWithEmail(emailAuthEntity)
}