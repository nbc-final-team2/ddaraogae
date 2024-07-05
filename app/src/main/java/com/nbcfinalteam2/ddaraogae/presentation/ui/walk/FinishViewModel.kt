package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.CheckStampConditionUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertWalkingDataUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FinishViewModel @Inject constructor(
    private val insertWalkingDataUseCase: InsertWalkingDataUseCase,
    private val checkStampConditionUseCase: CheckStampConditionUseCase
) : ViewModel() {

    private val _insertEvent = MutableSharedFlow<DefaultEvent>()
    val insertEvent: SharedFlow<DefaultEvent> = _insertEvent.asSharedFlow()

    private val _stampEvent = MutableSharedFlow<DefaultEvent>()
    val stampEvent: SharedFlow<DefaultEvent> = _stampEvent.asSharedFlow()

    private val _stampList = MutableSharedFlow<List<StampEntity>>()
    val stampList: SharedFlow<List<StampEntity>> = _stampList.asSharedFlow()

    private val _finishUiState = MutableStateFlow(FinishUiState.init())
    val finishUiState: StateFlow<FinishUiState> = _finishUiState.asStateFlow()


    fun insertWalkingData(walkDataSet: Pair<List<WalkingInfo>, ByteArray>) {
        _finishUiState.value = FinishUiState(true)
        viewModelScope.launch {
            val walkingDataList = walkDataSet.first.map {
                WalkingEntity(
                    it.id,
                    it.dogId,
                    it.timeTaken,
                    it.distance,
                    it.startDateTime,
                    it.endDateTime,
                    it.walkingImage
                )
            }
            val image = walkDataSet.second

            try {
                for(walkingData in walkingDataList) {
                    insertWalkingDataUseCase.invoke(walkingData, image)
                }
                _insertEvent.emit(DefaultEvent.Success)
            } catch (e: Exception) {
                _insertEvent.emit(DefaultEvent.Failure(R.string.msg_walk_upload_fail))
            } finally {
                _finishUiState.value = FinishUiState(false)
            }
        }
    }

    fun checkStampCondition(date: Date) {
        _finishUiState.value = FinishUiState(true)
        viewModelScope.launch {
            try {
                val result = checkStampConditionUseCase(date)
                _stampEvent.emit(DefaultEvent.Success)
                _stampList.emit(result)
            } catch (e: Exception) {
                _stampEvent.emit(DefaultEvent.Failure(R.string.msg_check_stamp_fail))
            } finally {
                _finishUiState.value = FinishUiState(false)
            }
        }
    }
}