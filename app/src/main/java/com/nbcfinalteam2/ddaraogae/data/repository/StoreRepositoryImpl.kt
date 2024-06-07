package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.SearchApiService
import com.nbcfinalteam2.ddaraogae.data.mapper.StoreMapper
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.StoreRepository
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val storeApiService: SearchApiService
) : StoreRepository {
    override suspend fun getStoreData(lng: String, lat: String): List<StoreEntity> {
        val storeResponseForHospital = storeApiService.getStoreForHospital(x = lat, y = lng)
        val storeResponseForFood = storeApiService.getStoreForFood(x = lat, y = lng)
        val storeItemList = StoreMapper.toStoreData(storeResponseForHospital, storeResponseForFood)
        return storeItemList
    }
}