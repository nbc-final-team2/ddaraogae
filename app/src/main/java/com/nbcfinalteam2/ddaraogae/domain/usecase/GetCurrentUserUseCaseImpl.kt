package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.UserEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository

class GetCurrentUserUseCaseImpl(
    private val authRepository: AuthRepository
): GetCurrentUserUseCase {
    override suspend fun invoke(): UserEntity? = authRepository.getCurrentUser()
}