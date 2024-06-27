package com.nbcfinalteam2.ddaraogae.presentation.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.AlarmEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAlarmUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetAlarmListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertAlarmUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateAlarmUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.toModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val insertAlarmUseCase: InsertAlarmUseCase,
    private val getAlarmListUseCase: GetAlarmListUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase
): ViewModel() {

    private val _alarmUiState = MutableStateFlow(AlarmUiState.init())
    val alarmUiState: StateFlow<AlarmUiState> = _alarmUiState.asStateFlow()

    init {
        collectAlarmList()
    }

    private fun collectAlarmList() = viewModelScope.launch {
        getAlarmListUseCase()
            .map { alarmEntityList ->
            alarmEntityList.map { it.toModel() }
            }
            .collect { alarmModelList ->
                _alarmUiState.update { prev ->
                    prev.copy(
                        alarmList = alarmModelList
                    )
                }
            }
    }

    fun insertAlarm(setTime: Int) = viewModelScope.launch {
        runCatching {
            insertAlarmUseCase(
                AlarmEntity(-1, setTime)
            )
        }
    }

    fun deleteAlarm(alarmId: Int) = viewModelScope.launch {
        runCatching {
            deleteAlarmUseCase(alarmId = alarmId)
        }
    }



}