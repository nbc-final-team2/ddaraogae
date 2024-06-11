package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.dto.StoreDto
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity

object StoreMapper {
    fun toStoreData(storeResponse: StoreDto): List<StoreEntity>? {
        val resultForStore = storeResponse.documents?.map {
            StoreEntity(
                id = it.id,
                placeName = it.placeName,
                categoryGroupName = it.categoryGroupName,
                address = it.roadAddressName,
                phone = it.phone,
            )
        }
        return resultForStore
    }
}