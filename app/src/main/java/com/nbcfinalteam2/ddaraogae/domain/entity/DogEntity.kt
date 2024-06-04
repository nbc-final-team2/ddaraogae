package com.nbcfinalteam2.ddaraogae.domain.entity

data class DogEntity(
    val id: String,
    val name: String,
    val gender: Int, //0: Male, 1: Female
    val age: Int?,
    val lineage: String?,
    val memo: String?,
    val thumbnailUrl: String?,
)
