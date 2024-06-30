package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampInfoListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampListByPeriodUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.StampModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class StampDetailViewModel @Inject constructor(
    private val getStampListByPeriodUseCase: GetStampListByPeriodUseCase,
    private val getStampInfoListUseCase: GetStampInfoListUseCase
) : ViewModel() {

    private val _stampDetail = MutableLiveData<StampModel>()
    val stampDetail: LiveData<StampModel> = _stampDetail

    private val _stampList = MutableLiveData<List<StampModel>>()
    val stampList: LiveData<List<StampModel>> = _stampList

    fun loadStampDetail(stampId: Int) {
        viewModelScope.launch {
            val stampInfo = getStampInfoListUseCase().find { it.num == stampId }
            if (stampInfo != null) {
                _stampDetail.value = StampModel(
                    id = null,
                    stampNum = null,
                    getDateTime = null,
                    name = stampInfo.title,
                    num = stampInfo.num,
                    title = stampInfo.title,
                    description = stampInfo.description
                )

                val currentDate = Date()
                val startDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, 2023)
                    set(Calendar.MONTH, Calendar.JUNE)
                    set(Calendar.DAY_OF_MONTH, 1)
                }.time

                val stampEntities = getStampListByPeriodUseCase(startDate, currentDate)
                val filteredEntities = stampEntities.filter { it.name == stampInfo.title }
                val stampModels = filteredEntities.map { entity ->
                    StampModel(
                        id = entity.id,
                        stampNum = filteredEntities.size,
                        getDateTime = entity.getDateTime,
                        name = entity.name,
                        num = stampInfo.num,
                        title = stampInfo.title,
                        description = stampInfo.description
                    )
                }
                _stampList.value = stampModels
            }
        }
    }
}