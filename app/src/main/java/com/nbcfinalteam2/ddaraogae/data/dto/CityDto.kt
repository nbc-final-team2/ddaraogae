package com.nbcfinalteam2.ddaraogae.data.dto

import com.google.gson.annotations.SerializedName

data class CityDto(
    @SerializedName("documents") val documents: List<Address>
)

data class Address(
    @SerializedName("region_1depth_name") val region1depthName: String,
    @SerializedName("region_2depth_name") val region2depthName: String,
    @SerializedName("region_3depth_name") val region3depthName: String,
    @SerializedName("x") val x: Double,
    @SerializedName("y") val y: Double,
)