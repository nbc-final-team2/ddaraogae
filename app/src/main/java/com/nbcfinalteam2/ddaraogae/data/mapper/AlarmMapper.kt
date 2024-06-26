package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.model.AlarmPreference
import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity

object AlarmMapper {
    fun AlarmPreference.toEntity() = AlarmEntity(
        id = id,
        setTime = setTime
    )

    fun AlarmEntity.toPreference() = AlarmPreference(
        id = id,
        setTime = setTime
    )
}