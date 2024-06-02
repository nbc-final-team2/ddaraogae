package com.nbcfinalteam2.ddaraogae.data.dto

import java.util.Date

data class WalkingDto(
    val id: String,
    val dogId: Long,
    val timeTaken: Int,
    val distance: Double,
    val startDateTime: Date,
    val endDateTime: Date,
    val path: String,
)
