package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.StampInfoEntity

interface GetStampInfoListUseCase {
    operator fun invoke(): List<StampInfoEntity>
}