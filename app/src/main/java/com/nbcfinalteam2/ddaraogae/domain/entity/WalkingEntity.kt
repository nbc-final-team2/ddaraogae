package com.nbcfinalteam2.ddaraogae.domain.entity

data class WalkingEntity (
    val id: String,
    val dogId: String,
    val timeTaken: Int,
    val distance: Double,
    val startDateTime: Long,
    val endDateTime: Long,
    val path: String,
)