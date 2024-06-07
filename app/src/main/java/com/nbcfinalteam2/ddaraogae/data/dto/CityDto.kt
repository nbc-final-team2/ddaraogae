package com.nbcfinalteam2.ddaraogae.data.dto

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("documents") val documents: List<Address>?
)

data class Address(
    @SerializedName("region_1depth_name") val region_1depth_name: String,
    @SerializedName("region_2depth_name") val region_2depth_name: String,
    @SerializedName("region_3depth_name") val region_3depth_name: String,
    @SerializedName("x") val x: Double,
    @SerializedName("y") val y: Double,
)