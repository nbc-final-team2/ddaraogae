package com.nbcfinalteam2.ddaraogae.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlarmPreference(
    val id: Int,
    val setTime: Int,
    val uid: String
): Parcelable
