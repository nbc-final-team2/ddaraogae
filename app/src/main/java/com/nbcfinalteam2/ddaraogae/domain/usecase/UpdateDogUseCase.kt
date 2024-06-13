package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

interface UpdateDogUseCase {
    suspend operator fun invoke(dogEntity: DogEntity, byteImage: ByteArray?)
}