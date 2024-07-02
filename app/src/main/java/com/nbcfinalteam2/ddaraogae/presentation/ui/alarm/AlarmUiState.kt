package com.nbcfinalteam2.ddaraogae.presentation.ui.alarm

import com.nbcfinalteam2.ddaraogae.presentation.model.AlarmModel

data class AlarmUiState(
    val alarmList: List<AlarmModel>
) {
    companion object {
        fun init() = AlarmUiState(
            alarmList = emptyList()
        )
    }
}
