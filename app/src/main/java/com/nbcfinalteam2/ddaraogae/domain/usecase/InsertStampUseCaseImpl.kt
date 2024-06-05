package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.StampRepository

class InsertStampUseCaseImpl(
    private val stampRepository: StampRepository
): InsertStampUseCase {
    override suspend fun invoke(stampEntity: StampEntity) = stampRepository.insertStamp(stampEntity)
}