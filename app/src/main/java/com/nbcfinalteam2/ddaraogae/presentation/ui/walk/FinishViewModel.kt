package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.CheckStampConditionUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertWalkingDataUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingUiModel
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

    private val _taskState = MutableStateFlow<InsertTaskState>(InsertTaskState.Idle)
    val taskState: StateFlow<InsertTaskState> = _taskState.asStateFlow()

    private val _stampState = MutableStateFlow<StampTaskState>(StampTaskState.Idle)
    val stampState: StateFlow<StampTaskState> = _stampState.asStateFlow()

    private val _stampList = MutableSharedFlow<List<StampEntity>>()
    val stampList: SharedFlow<List<StampEntity>> = _stampList.asSharedFlow()


    fun insertWalkingData(walk: WalkingUiModel, image: ByteArray) {
        viewModelScope.launch {
            val walkingData = walk.let {
                WalkingEntity(
                    it.id,
                    it.dogId,
                    it.timeTaken,
                    it.distance,
                    it.startDateTime,
                    it.endDateTime,
                    it.url
                )
            }

            try {
                insertWalkingDataUseCase.invoke(walkingData, image)
                _taskState.value = InsertTaskState.Success
            } catch (e: Exception) {
                _taskState.value = InsertTaskState.Error(e)
            }
        }
    }

    fun checkStampCondition(date: Date) {
        viewModelScope.launch {
            try {
                val result = checkStampConditionUseCase(date)
                Log.d("viewmodel", result.toString())
                _stampState.value = StampTaskState.Success
                _stampList.emit(result)
            } catch (e: Exception) {
                _stampState.value = StampTaskState.Error(e)
                e.printStackTrace()
            }
        }
    }
}