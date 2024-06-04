package com.nbcfinalteam2.ddaraogae.data.dto

import com.google.gson.annotations.SerializedName

data class Dust(val list: List<DustList>)

data class DustList (
    val components: DustComponents,
)

data class DustComponents(
    @SerializedName("pm2_5")
    val pm25: Double, //초미세먼지
    val pm10: Double, //미세먼지
)

