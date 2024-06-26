package com.nbcfinalteam2.ddaraogae.presentation.model

import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity

data class AlarmModel(
    val id: Long,
    val setTime: Int
)


fun AlarmModel.toEntity() = AlarmEntity(
    id = id,
    setTime = setTime
)

fun AlarmEntity.toModel() = AlarmModel(
    id = id,
    setTime = setTime
)