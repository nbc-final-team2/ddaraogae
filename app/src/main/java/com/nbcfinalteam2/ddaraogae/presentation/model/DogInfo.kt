package com.nbcfinalteam2.ddaraogae.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DogInfo (
    val id: String,
    val name: String,
    val gender: Int, // 0: Mail, 1: Female
    val age: Int?,
    val lineage: String?,
    val memo: String?,
    val thumbnailUrl: String?,
    var isSelected: Boolean = false
) : Parcelable