package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.SearchApiService
import com.nbcfinalteam2.ddaraogae.data.mapper.StoreMapper
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.StoreRepository
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val storeApiService: SearchApiService
) : StoreRepository {

    override suspend fun getStoreData(lat: String, lng: String): List<StoreEntity>? {
        val storeResponseForFood = storeApiService.getStore(x = lng, y = lat, query = "애견동반")
        val storeItemList = StoreMapper.toStoreData(storeResponseForFood)
        return storeItemList
    }

    override suspend fun getHospitalData(lat: String, lng: String): List<StoreEntity>? {
        val storeResponseForHospital = storeApiService.getStore(x = lng, y = lat, query = "동물병원")
        val hospitalItemList = StoreMapper.toStoreData(storeResponseForHospital)
        return hospitalItemList
    }
}