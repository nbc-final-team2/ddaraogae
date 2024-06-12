package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import javax.inject.Inject

class IsCurrentUserEmailVerifiedUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): IsCurrentUserEmailVerifiedUseCase {
    override suspend fun invoke(): Boolean = authRepository.isCurrentUserEmailVerified()

}