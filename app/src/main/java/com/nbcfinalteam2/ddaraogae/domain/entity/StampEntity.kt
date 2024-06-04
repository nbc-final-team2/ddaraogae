package com.nbcfinalteam2.ddaraogae.domain.entity

import java.util.Date

data class StampEntity(
    val id: String,
    val spotId: String,
    val walkingId: String,
    val dogId: String,
    val getDateTime: Date,
    val latitude: Double,
    val longitude: Double,
    val name: String
)
