package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.SearchApiService
import com.nbcfinalteam2.ddaraogae.data.mapper.StoreMapper
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.StoreRepository

class StoreRepositoryImpl(private val storeApiService: SearchApiService) : StoreRepository {
    override suspend fun getStoreData(): List<StoreEntity> {
        val storeResponse = storeApiService.getStore()
        val storeItemList = StoreMapper.toStoreData(storeResponse)
        return storeItemList
    }
}