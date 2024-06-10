package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.StoreRepository
import javax.inject.Inject

class GetHospitalStoreDataUseCaseImpl @Inject constructor(
    private val storeRepository: StoreRepository
): GetStoreDataUseCase {
    override suspend fun invoke(lat: String, lng: String): List<StoreEntity> = storeRepository.getHospitalData(lat, lng)
}