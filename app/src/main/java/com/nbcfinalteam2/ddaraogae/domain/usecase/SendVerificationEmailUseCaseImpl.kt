package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import javax.inject.Inject

class SendVerificationEmailUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): SendVerificationEmailUseCase {
    override suspend fun invoke() = authRepository.sendVerificationEmail()
}