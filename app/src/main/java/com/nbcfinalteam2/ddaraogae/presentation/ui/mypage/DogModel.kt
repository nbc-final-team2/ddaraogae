package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import java.io.Serializable

data class DogModel(
    val id: String? = null,
    val name: String? = null,
    val gender:Boolean? = null,
    val age: Int? = null,
    val lineage: String? = null,
    val memo: String? = null,
    val thumbnailUrl: String? = null,
) : Serializable
