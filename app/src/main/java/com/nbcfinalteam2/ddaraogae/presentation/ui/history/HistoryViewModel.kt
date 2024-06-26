package com.nbcfinalteam2.ddaraogae.presentation.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getWalkingListByDogIdAndPeriodUseCase: GetWalkingListByDogIdAndPeriodUseCase
) : ViewModel() {
    private val _selectedDateState = MutableStateFlow<String>("날짜 선택")
    val selectedDateState: StateFlow<String> = _selectedDateState.asStateFlow()

    private val _selectDogState = MutableStateFlow<DogInfo?>(null)
    val selectDogState = _selectDogState.asStateFlow()

    private val _walkListState = MutableStateFlow<List<WalkingInfo>>(emptyList())
    val walkListState = _walkListState.asStateFlow()

    private val _loadWalkEvent = MutableSharedFlow<DefaultEvent>()
    val loadWalkEvent: SharedFlow<DefaultEvent> = _loadWalkEvent.asSharedFlow()

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0

    var isLoading = false
    private var currentPage = 0
    private val pageSize = 25

    fun setSelectedDate(year: Int, month: Int) {
        selectedYear = year
        selectedMonth = month
        _selectedDateState.value = "${year}년 ${month}월"
        currentPage = 0
        loadWalkData(year, month, reset = true)
    }

    fun setDogInfo(dog: DogInfo) {
        _selectDogState.value = dog
    }

    private fun loadWalkData(year: Int, month: Int, reset: Boolean = false) {
        viewModelScope.launch {
            try {
                isLoading = true
                val startDate = DateFormatter.getStartDateForAllDay(year, month)
                val endDate = DateFormatter.getEndDateForAllDay(year, month)

                val dogId = _selectDogState.value?.id ?: return@launch
                val walkEntities = getWalkingListByDogIdAndPeriodUseCase(dogId, startDate, endDate)
                val walkInfo = walkEntities.map {
                    WalkingInfo(
                        id = it.id,
                        dogId = it.dogId,
                        timeTaken = it.timeTaken,
                        distance = it.distance,
                        startDateTime = it.startDateTime,
                        endDateTime = it.endDateTime,
                        walkingImage = it.walkingImage
                    )
                }.sortedByDescending { it.startDateTime }

                if (reset) {
                    _walkListState.value = walkInfo.take(pageSize)
                    currentPage = 1
                } else {
                    val updatedList = _walkListState.value.toMutableList()
                    updatedList.addAll(walkInfo.drop(currentPage * pageSize).take(pageSize))
                    _walkListState.value = updatedList
                    currentPage++
                }
                _loadWalkEvent.emit(DefaultEvent.Success)

            } catch (exception: Exception) {
                exception.printStackTrace()
                _loadWalkEvent.emit(DefaultEvent.Failure(R.string.msg_load_walking_data_fail))
            } finally {
                isLoading = false
            }
        }
    }

    fun loadMoreWalkData() {
        loadWalkData(selectedYear, selectedMonth)
    }

    fun getSelectedYearMonth(): Pair<Int, Int> {
        return Pair(selectedYear, selectedMonth)
    }
}