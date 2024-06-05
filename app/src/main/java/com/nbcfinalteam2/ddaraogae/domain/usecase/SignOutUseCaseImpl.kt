package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository

class SignOutUseCaseImpl(
    private val authRepository: AuthRepository
): SignOutUseCase {
    override suspend fun invoke() = authRepository.signOut()
}