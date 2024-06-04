package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.data.dto.StampDto
import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import java.util.Date

object FirebaseMapper {
    fun DogDto.toEntity(id: String) = DogEntity(
        id = id,
        name = name?:"",
        gender = gender?:0,
        age = age,
        lineage = lineage,
        memo = memo,
        thumbnailUrl = thumbnailUrl
    )

    fun DogEntity.toDto() = DogDto(
        name = name,
        gender = gender,
        age = gender,
        lineage = lineage,
        memo = memo,
        thumbnailUrl = thumbnailUrl
    )

    fun StampDto.toEntity(id: String) = StampEntity(
        id = id,
        stampNum = stampNum?:-1,
        getDateTime = getDateTime?: Date(),
        name = name?:""
    )

    fun StampEntity.toDto() = StampDto(
        stampNum = stampNum,
        getDateTime = getDateTime,
        name = name
    )

    fun WalkingDto.toEntity(id: String) = WalkingEntity(
        id = id,
        dogId = dogId?:"",
        timeTaken = timeTaken?:0,
        distance = distance?:0.0,
        startDateTime = startDateTime?:Date(),
        endDateTime = endDateTime?:Date(),
        path = path?:""
    )

    fun WalkingEntity.toDto() = WalkingDto(
        dogId = dogId,
        timeTaken = timeTaken,
        distance = distance,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        path = path
    )
}