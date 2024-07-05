package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import java.util.Date

interface CheckStampConditionUseCase {
    suspend operator fun invoke(date: Date): List<StampEntity>
}