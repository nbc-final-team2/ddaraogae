package com.nbcfinalteam2.ddaraogae.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class StampEntity(
    val id: String?,
    val stampNum: Int?,
    val getDateTime: Date?,
    val name: String?,
) : Parcelable
