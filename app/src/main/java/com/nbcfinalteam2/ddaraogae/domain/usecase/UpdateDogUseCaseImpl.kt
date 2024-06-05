package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.DogRepository

class UpdateDogUseCaseImpl(
    private val dogRepository: DogRepository
): UpdateDogUseCase {
    override suspend fun invoke(dogEntity: DogEntity) = dogRepository.updateDog(dogEntity)
}