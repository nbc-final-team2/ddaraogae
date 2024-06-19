package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getWalkingListByDogIdAndPeriodUseCase: GetWalkingListByDogIdAndPeriodUseCase
) : ViewModel() {
    private val _selectedDateState = MutableStateFlow<String>("날짜 선택")
    val selectedDateState:StateFlow<String> = _selectedDateState.asStateFlow()

    private val _selectDogState = MutableStateFlow<DogInfo?>(null)
    val selectDogState = _selectDogState.asStateFlow()

    private val _walkListState = MutableStateFlow<List<WalkingInfo>>(emptyList())
    val walkListState = _walkListState.asStateFlow()

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0

    fun setSelectedDate(year: Int, month: Int) {
        selectedYear = year
        selectedMonth = month
        _selectedDateState.value = "${year}년 ${month}월"
        loadWalkData(year, month)
    }

    fun setDogInfo(dog: DogInfo) {
        _selectDogState.value = dog
    }

    private fun loadWalkData(year: Int, month: Int) {
        viewModelScope.launch {
            try {
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
                }
                _walkListState.value = walkInfo
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }


    fun getSelectedYearMonth(): Pair<Int, Int> {
        return Pair(selectedYear, selectedMonth)
    }
}