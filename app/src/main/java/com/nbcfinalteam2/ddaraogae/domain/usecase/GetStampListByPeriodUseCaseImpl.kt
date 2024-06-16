package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.StampRepository
import java.util.Date
import javax.inject.Inject

class GetStampListByPeriodUseCaseImpl @Inject constructor(
    private val stampRepository: StampRepository
) : GetStampListByPeriodUseCase {
    override suspend fun invoke(start: Date, end: Date): List<StampEntity> =
        stampRepository.getStampListByPeriod(start, end)
}