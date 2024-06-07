package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.dto.Store
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity

object StoreMapper {
    fun toStoreData(storeResponseForHospital: Store, storeResponseForFood: Store): List<StoreEntity> {
        val resultForHospital = storeResponseForHospital.documents.map {
            StoreEntity(
                placeName = it.place_name,
                categoryGroupName = it.category_group_name ?: "none",
                address = it.road_address_name,
                phone = it.phone,
                lat = it.y,
                lng = it.x
            )
        }
        val resultForFood = storeResponseForFood.documents.map {
            StoreEntity(
                placeName = it.place_name,
                categoryGroupName = it.category_group_name ?: "none",
                address = it.road_address_name,
                phone = it.phone,
                lat = it.y,
                lng = it.x
            )
        }
        val combinedList = (resultForHospital + resultForFood).toSet().toList()

        return combinedList
    }
}