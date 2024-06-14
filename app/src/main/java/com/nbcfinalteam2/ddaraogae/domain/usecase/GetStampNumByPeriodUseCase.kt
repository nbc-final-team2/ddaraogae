package com.nbcfinalteam2.ddaraogae.domain.usecase

import java.util.Date

interface GetStampNumByPeriodUseCase {
    suspend operator fun invoke(start: Date, end: Date): Int
}