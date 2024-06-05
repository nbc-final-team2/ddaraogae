package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.DogRepository

class GetDogListUseCaseImpl(
    private val dogRepository: DogRepository
): GetDogListUseCase {
    override suspend fun invoke(): List<DogEntity> = dogRepository.getDogList()
}