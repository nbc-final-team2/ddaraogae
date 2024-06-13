package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

interface GetDogListUseCase {
    suspend operator fun invoke(): List<DogEntity>
}