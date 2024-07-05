package com.nbcfinalteam2.ddaraogae.domain.usecase

interface DeleteDogUseCase {
    suspend operator fun invoke(dogId: String)
}