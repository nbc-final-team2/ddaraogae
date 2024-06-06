package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.DogRepository
import javax.inject.Inject

class GetDogListUseCaseImpl @Inject constructor(
    private val dogRepository: DogRepository
): GetDogListUseCase {
    override suspend fun invoke(): List<DogEntity> = dogRepository.getDogList()
}