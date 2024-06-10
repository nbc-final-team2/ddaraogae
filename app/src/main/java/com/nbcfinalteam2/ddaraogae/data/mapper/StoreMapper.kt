package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.dto.Store
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity

object StoreMapper {
    fun toStoreData(storeResponseForHospital: Store, storeResponseForFood: Store): List<StoreEntity> {
        val resultForHospital = storeResponseForHospital.documents.map {
            StoreEntity(
                id = it.id,
                placeName = it.placeName,
                categoryGroupName = it.categoryGroupName ?: "none",
                address = it.roadAddressName,
                phone = it.phone,
            )
        }
        val resultForFood = storeResponseForFood.documents.map {
            StoreEntity(
                id = it.id,
                placeName = it.placeName,
                categoryGroupName = it.categoryGroupName ?: "none",
                address = it.roadAddressName,
                phone = it.phone,
            )
        }
        val combinedList = (resultForHospital + resultForFood).distinctBy { it.id }

        return combinedList
    }
}