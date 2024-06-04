package com.nbcfinalteam2.ddaraogae.data.dto

import com.google.gson.annotations.SerializedName

data class Dust(
    @SerializedName("list") val list: List<DustList>?
)

data class DustList (
    @SerializedName("components") val components: DustComponents?
)

data class DustComponents(
    @SerializedName("pm2_5") val pm25: Double?, //초미세먼지
    @SerializedName("pm10") val pm10: Double? //미세먼지
)

