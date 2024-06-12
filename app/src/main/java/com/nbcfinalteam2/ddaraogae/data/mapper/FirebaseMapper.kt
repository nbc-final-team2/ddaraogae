package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.data.dto.StampDto
import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity

object FirebaseMapper {
    fun DogDto.toEntity(id: String) = DogEntity(
        id = id,
        name = name,
        gender = gender,
        age = age,
        lineage = lineage,
        memo = memo,
        thumbnailUrl = thumbnailUrl
    )

    fun DogEntity.toDto() = DogDto(
        name = name,
        gender = gender,
        age = age,
        lineage = lineage,
        memo = memo,
        thumbnailUrl = thumbnailUrl
    )

    fun StampDto.toEntity(id: String) = StampEntity(
        id = id,
        stampNum = stampNum,
        getDateTime = getDateTime,
        name = name
    )

    fun StampEntity.toDto() = StampDto(
        stampNum = stampNum,
        getDateTime = getDateTime,
        name = name
    )

    fun WalkingDto.toEntity(id: String) = WalkingEntity(
        id = id,
        dogId = dogId,
        timeTaken = timeTaken,
        distance = distance,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        walkingImage = walkingImage
    )

    fun WalkingEntity.toDto() = WalkingDto(
        dogId = dogId,
        timeTaken = timeTaken,
        distance = distance,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        walkingImage = walkingImage
    )
}