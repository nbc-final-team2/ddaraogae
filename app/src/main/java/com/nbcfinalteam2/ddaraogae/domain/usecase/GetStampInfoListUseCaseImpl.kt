package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.StampInfoEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.StampRepository
import javax.inject.Inject

class GetStampInfoListUseCaseImpl @Inject constructor(
    private val stampRepository: StampRepository
): GetStampInfoListUseCase{
    override fun invoke(): List<StampInfoEntity> = stampRepository.getStampInfoList()
}