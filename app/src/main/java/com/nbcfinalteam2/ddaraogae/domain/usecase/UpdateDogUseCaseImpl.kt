package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.DogRepository
import javax.inject.Inject

class UpdateDogUseCaseImpl @Inject constructor(
    private val dogRepository: DogRepository
): UpdateDogUseCase {
    override suspend fun invoke(dogEntity: DogEntity, byteImage: ByteArray?) = dogRepository.updateDog(dogEntity, byteImage)
}