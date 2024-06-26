package com.nbcfinalteam2.ddaraogae.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlarmPreference(
    val id: String,
    val setTime: Int
): Parcelable
