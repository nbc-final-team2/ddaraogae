package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity

interface InsertStampUseCase {
    suspend operator fun invoke(stampEntity: StampEntity)
}