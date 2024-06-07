package com.nbcfinalteam2.ddaraogae.data.dto

import com.google.gson.annotations.SerializedName

data class Store(
    @SerializedName("documents") val documents: List<Document>,
    @SerializedName("meta") val meta: Meta,
)

data class Document(
    @SerializedName("address_name") val address_name: String,
    @SerializedName("category_group_code") val category_group_code: String?,
    @SerializedName("category_group_name") val category_group_name: String?,
    @SerializedName("category_name") val category_name: String,
    @SerializedName("distance") val distance: String,
    @SerializedName("id") val id: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("place_name") val place_name: String,
    @SerializedName("place_url") val place_url: String,
    @SerializedName("road_address_name") val road_address_name: String,
    @SerializedName("x") val x: String, //longitude
    @SerializedName("y") val y: String  //latitude
)

data class Meta(
    @SerializedName("is_end") val is_end: Boolean,
    @SerializedName("pageable_count") val pageable_count: Int,
    @SerializedName("same_name") val same_name: SameName,
    @SerializedName("total_count") val total_count: Int,
)

data class SameName(
    @SerializedName("keyword") val keyword: String?,
    @SerializedName("region") val region: List<String>?,
    @SerializedName("selected_region") val selected_region: String?,
)
