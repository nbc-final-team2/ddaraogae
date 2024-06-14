package com.nbcfinalteam2.ddaraogae.domain.usecase

import java.util.Date

interface GetStampNumByPeriodUseCase {
    suspend operator fun invoke(dogId: String, start: Date, end: Date): Int
}