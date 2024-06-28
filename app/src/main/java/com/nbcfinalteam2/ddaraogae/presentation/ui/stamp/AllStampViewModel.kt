package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampInfoListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampListByPeriodUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.StampListModel
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
    private val getStampInfoListUseCase: GetStampInfoListUseCase
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
        runCatching {
            val stampEntities = getStampInfoListUseCase()

            val stampModels = stampEntities.filter { it.num != 0 && it.num <= 8 }.map { entity ->
                StampModel(
                    id = null,
                    stampNum = null,
                    getDateTime = null,
                    name = null,
                    num = entity.num,
                    title = entity.title
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
