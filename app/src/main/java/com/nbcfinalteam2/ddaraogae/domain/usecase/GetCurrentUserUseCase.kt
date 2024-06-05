package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.UserEntity

interface GetCurrentUserUseCase {
    suspend operator fun invoke(): UserEntity?
}