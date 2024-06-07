package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.DogRepository
import javax.inject.Inject

class InsertDogUseCaseImpl @Inject constructor(
    private val dogRepository: DogRepository
): InsertDogUseCase {
    override suspend fun invoke(dogEntity: DogEntity) = dogRepository.insertDog(dogEntity)
}