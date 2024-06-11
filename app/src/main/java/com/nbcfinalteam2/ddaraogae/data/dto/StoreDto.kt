package com.nbcfinalteam2.ddaraogae.data.dto

import com.google.gson.annotations.SerializedName

data class StoreDto(
    @SerializedName("documents") val documents: List<Document>?,
)

data class Document(
    @SerializedName("category_group_name") val categoryGroupName: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("place_name") val placeName: String?,
    @SerializedName("road_address_name") val roadAddressName: String?,
)
