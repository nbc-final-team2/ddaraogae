package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity

interface GetStoreDataUseCase {
    suspend operator fun invoke(lat: String, lng: String): List<StoreEntity>?
}