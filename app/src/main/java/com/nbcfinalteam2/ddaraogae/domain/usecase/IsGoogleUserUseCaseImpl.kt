package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import javax.inject.Inject

class IsGoogleUserUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): IsGoogleUserUseCase {
    override fun invoke(): Boolean = authRepository.isGoogleUser()
}