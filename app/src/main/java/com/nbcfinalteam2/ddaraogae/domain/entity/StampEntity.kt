package com.nbcfinalteam2.ddaraogae.domain.entity

data class StampEntity(
    val id: String,
    val spotId: String,
    val walkingId: String,
    val dogId: String,
    val getDateTime: String,
    val latitude: Double,
    val longitude: Double,
    val name: String
)
