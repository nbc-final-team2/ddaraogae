package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import java.util.Date

interface GetStampListByPeriodUseCase {
    suspend operator fun invoke(start: Date, end: Date): List<StampEntity>
}