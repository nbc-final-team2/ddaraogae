package com.nbcfinalteam2.ddaraogae.presentation.model

import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity

data class AlarmModel(
    val id: Int,
    val setHour: Int,
    val setMinute: Int,
    val isPm: Boolean
)

fun AlarmEntity.toModel(): AlarmModel {
    var setHour = 12
    var setMinute = 0
    var isPm = false

    var totalTime = setTime

    if(totalTime>=720) {
        isPm = true
        totalTime -= 720
    }

    setHour = totalTime/60
    setMinute = totalTime%60

    return AlarmModel(
        id = id,
        setHour = setHour,
        setMinute = setMinute,
        isPm = isPm
    )

}