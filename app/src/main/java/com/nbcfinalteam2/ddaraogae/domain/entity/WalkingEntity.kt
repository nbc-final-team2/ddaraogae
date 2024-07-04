package com.nbcfinalteam2.ddaraogae.domain.entity

import java.util.Date

data class WalkingEntity (
    val id: String?,
    val timeTaken: Int?,
    val distance: Double?,
    val startDateTime: Date?,
    val endDateTime: Date?,
    val walkingImage: String?,
)