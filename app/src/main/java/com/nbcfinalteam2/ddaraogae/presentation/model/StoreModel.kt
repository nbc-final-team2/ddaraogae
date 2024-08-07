package com.nbcfinalteam2.ddaraogae.presentation.model

import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity

data class StoreUiModel(
    val id: String?,
    val placeName: String?,
    val categoryGroupName: String?,
    val address: String?,
    val phone: String?,
    val lat: String?,
    val lng: String?,
)

fun StoreEntity.toUiModel() = StoreUiModel(
    id = id,
    placeName = placeName,
    categoryGroupName = categoryGroupName,
    address = address,
    phone = phone,
    lat = lat,
    lng = lng
)
