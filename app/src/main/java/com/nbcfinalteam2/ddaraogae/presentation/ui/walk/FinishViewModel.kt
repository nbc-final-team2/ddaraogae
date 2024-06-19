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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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


    fun insertWalkingData(walk: WalkingInfo, image: ByteArray) {
        viewModelScope.launch {
            val walkingData = walk.let {
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

            try {
                insertWalkingDataUseCase.invoke(walkingData, image)
                _insertEvent.emit(DefaultEvent.Success)
            } catch (e: Exception) {
                _insertEvent.emit(DefaultEvent.Failure(R.string.msg_walk_upload_fail))
            }
        }
    }

    fun checkStampCondition(date: Date) {
        viewModelScope.launch {
            try {
                val result = checkStampConditionUseCase(date)
                _stampEvent.emit(DefaultEvent.Success)
                _stampList.emit(result)
            } catch (e: Exception) {
                _stampEvent.emit(DefaultEvent.Failure(R.string.msg_check_stamp_fail))
            }
        }
    }
}