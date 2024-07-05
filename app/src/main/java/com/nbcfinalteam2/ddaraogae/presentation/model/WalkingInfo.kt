package com.nbcfinalteam2.ddaraogae.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class WalkingInfo(
    val id: String?,
    val dogId: String?,
    val timeTaken: Int?,
    val distance: Double?,
    val startDateTime: Date?,
    val endDateTime: Date?,
    val walkingImage: String?
): Parcelable