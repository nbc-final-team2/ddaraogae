package com.nbcfinalteam2.ddaraogae.presentation.ui.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DogItemModel(
    val id: String?,
    val name: String?,
    val gender:Int?,
    val age: Int? = null,
    val lineage: String? = null,
    val memo: String? = null,
    val thumbnailUrl: String? = null,
) : Parcelable
