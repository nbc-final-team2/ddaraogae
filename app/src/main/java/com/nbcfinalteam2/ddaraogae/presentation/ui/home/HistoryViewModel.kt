package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
) : ViewModel() {

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _dogInfo = MutableLiveData<DogInfo>()
    val dogInfo: LiveData<DogInfo> get() = _dogInfo

    private val _walkData = MutableLiveData<List<WalkingInfo>>()
    val walkData: LiveData<List<WalkingInfo>> get() = _walkData

    fun setSelectedDate(year: Int, month: Int) {
        viewModelScope.launch {
            _selectedDate.value = "${year}년 ${month}월"
        }
    }

    fun setDogInfo(dog: DogInfo) {
        _dogInfo.value = dog
    }

    private fun loadWalkData(year: Int, month: Int) {
        viewModelScope.launch {

        }
    }
}
