package com.nbcfinalteam2.ddaraogae.data.dto

import java.util.Date

data class StampDto(
    val id: String,
    val spotId: Long,
    val walkingId: Long,
    val dogId: Long,
    val getDateTime: Date,
    val latitude: Double,
    val longitude: Double,
    val name: String
)
