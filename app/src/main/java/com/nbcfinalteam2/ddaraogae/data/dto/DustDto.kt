package com.nbcfinalteam2.ddaraogae.data.dto

import com.google.gson.annotations.SerializedName

data class Dust(val list: List<DustList>)

data class DustList (
    val main : DustMain,
    val components: DustComponents,
)

data class DustMain(
    /**
     * Air Quality Index.
     * Possible values: 1, 2, 3, 4, 5.
     * Where 1 = Good, 2 = Fair, 3 = Moderate, 4 = Poor, 5 = Very Poor.
     */
    val aqi : Int //공기질 레벨
)

data class DustComponents(
    @SerializedName("pm2_5")
    val pm25: Double, //
    val pm10: Double,
)

