package com.nbcfinalteam2.ddaraogae.domain.usecase

interface IsCurrentUserEmailVerifiedUseCase {
    suspend operator fun invoke(): Boolean
}