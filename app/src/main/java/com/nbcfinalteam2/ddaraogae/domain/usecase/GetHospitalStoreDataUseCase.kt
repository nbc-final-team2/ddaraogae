package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity

interface GetHospitalStoreDataUseCase {
    suspend operator fun invoke(lat: String, lng: String): List<StoreEntity>
}