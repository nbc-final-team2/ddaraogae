package com.nbcfinalteam2.ddaraogae.data.mapper

import com.google.gson.Gson
import com.nbcfinalteam2.ddaraogae.data.model.AlarmPreference
import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity

object AlarmMapper {
    private val gson = Gson()

    private fun AlarmPreference.toEntity() = AlarmEntity(
        id = id,
        setTime = setTime
    )

    private fun AlarmEntity.toPreference() = AlarmPreference(
        id = id,
        setTime = setTime
    )

    fun entityToJson(alarmEntity: AlarmEntity) = gson.toJson(alarmEntity.toPreference())

    fun jsonToEntity(json: String) = gson.fromJson(json, AlarmPreference::class.java).toEntity()
}