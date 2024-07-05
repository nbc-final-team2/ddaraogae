package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.UserEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): GetCurrentUserUseCase {
    override fun invoke(): UserEntity? = authRepository.getCurrentUser()
}