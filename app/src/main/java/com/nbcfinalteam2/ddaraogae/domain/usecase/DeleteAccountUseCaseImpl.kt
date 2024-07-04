package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): DeleteAccountUseCase {
    override suspend fun invoke(credential: String) = authRepository.deleteAccount(credential)
}