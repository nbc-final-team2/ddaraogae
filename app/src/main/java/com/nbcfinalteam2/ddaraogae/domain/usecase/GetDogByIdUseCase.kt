package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

interface GetDogByIdUseCase {
    suspend operator fun invoke(dogId: String): DogEntity?
}