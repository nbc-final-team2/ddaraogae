package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampListByPeriodUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.StampModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AllStampViewModel @Inject constructor(
    private val getStampListByPeriodUseCase: GetStampListByPeriodUseCase
) : ViewModel() {

    private val _stampListState = MutableStateFlow<List<StampModel>>(emptyList())
    val stampListState: StateFlow<List<StampModel>> = _stampListState.asStateFlow()

    private val _loadStampEvent = MutableSharedFlow<DefaultEvent>()
    val loadStampEvent: SharedFlow<DefaultEvent> = _loadStampEvent.asSharedFlow()

    private val _updateStampEvent = MutableSharedFlow<DefaultEvent>()
    val updateStampEvent: SharedFlow<DefaultEvent> = _updateStampEvent.asSharedFlow()

    init {
        loadStampList()
    }

    fun loadStampList() = viewModelScope.launch {
        val startDate = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 1) // 2024년 6월 1일부터 도장이 기록이됩니다.
        }.time

        val endDate = Date()

        runCatching {
            val stampEntities = getStampListByPeriodUseCase(startDate, endDate)

            val stampCountMap = stampEntities.groupingBy { it.stampNum }.eachCount()

            val stampModels = stampCountMap.map { (stampNum, count) ->
                val firstEntity = stampEntities.first { it.stampNum == stampNum }
                StampModel(
                    id = firstEntity.id,
                    stampNum = count,
                    getDateTime = firstEntity.getDateTime,
                    name = firstEntity.name
                )
            }

            _stampListState.value = stampModels
        }.onSuccess {
            _loadStampEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _loadStampEvent.emit(DefaultEvent.Failure(R.string.home_all_stamp_msg_data_fail))
        }
    }
}