package com.nbcfinalteam2.ddaraogae.domain.entity

data class DogEntity(
    val id: Long,
    val name: String,
    val age: Int,
    val lineage: String,
    val memo: String,
    val thumbnailUrl: String,
)
