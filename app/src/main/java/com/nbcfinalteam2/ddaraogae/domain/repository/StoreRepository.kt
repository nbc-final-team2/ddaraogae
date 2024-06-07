package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity

interface StoreRepository {
    suspend fun getStoreData(lng: String, lat: String): List<StoreEntity>
}