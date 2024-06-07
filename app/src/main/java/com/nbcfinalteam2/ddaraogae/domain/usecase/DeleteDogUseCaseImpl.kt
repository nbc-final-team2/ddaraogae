package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.repository.DogRepository
import javax.inject.Inject

class DeleteDogUseCaseImpl @Inject constructor(
    private val dogRepository: DogRepository
): DeleteDogUseCase {
    override suspend fun invoke(dogId: String) = dogRepository.deleteDog(dogId)
}