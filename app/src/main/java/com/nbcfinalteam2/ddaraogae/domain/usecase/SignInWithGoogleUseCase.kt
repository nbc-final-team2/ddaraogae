package com.nbcfinalteam2.ddaraogae.domain.usecase

interface SignInWithGoogleUseCase {
    suspend operator fun invoke(idToken: String): Boolean
}