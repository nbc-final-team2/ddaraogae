package com.nbcfinalteam2.ddaraogae.presentation.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmController
import com.nbcfinalteam2.ddaraogae.presentation.model.toModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmController: AlarmController,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
): ViewModel() {

    val alarmUiState: StateFlow<AlarmUiState> = alarmController.getAlarmListFlow(getCurrentUserUseCase()?.uid!!)
        .map { entityList ->
            AlarmUiState( entityList.map { it.toModel() })
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AlarmUiState.init()
        )


    fun insertAlarm(setTime: Int) = alarmController.createAlarm(setTime, getCurrentUserUseCase()?.uid!!)

    fun updateAlarm(alarmId: Int, setTime: Int) = alarmController.updateAlarm(alarmId, setTime, getCurrentUserUseCase()?.uid!!)

    fun deleteAlarm(alarmId: Int) = alarmController.deleteAlarm(alarmId)

}