package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.dto.Store
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity

object StoreMapper {
    fun toStoreData(StoreList: Store): List<StoreEntity> {
        return StoreList.documents.map {
            StoreEntity(
                placeName = it.place_name,
                categoryGroupName = it.category_group_name ?: "none",
                address = it.address_name,
                phone = it.phone
            )
        }
    }
}