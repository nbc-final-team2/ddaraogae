package com.nbcfinalteam2.ddaraogae.domain.entity

data class DogEntity(
    val id: String,
    val name: String,
    val age: Int,
    val gender: Int, //0: Male, 1: Female
    val lineage: String,
    val memo: String,
    val thumbnailUrl: String,
)
