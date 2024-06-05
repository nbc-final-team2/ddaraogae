package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.DogRepository

class GetDogByIdUseCaseImpl(
    private val dogRepository: DogRepository
): GetDogByIdUseCase {
    override suspend fun invoke(dogId: String): DogEntity? = dogRepository.getDogById(dogId)
}