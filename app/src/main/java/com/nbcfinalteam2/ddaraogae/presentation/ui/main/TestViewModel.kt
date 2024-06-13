package com.nbcfinalteam2.ddaraogae.presentation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStoreDataUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * main activity에서 검색 api 테스트 할 때 사용하는 viewmodel 입니다.
 * 테스트 후 제거해주세요.
 */
@HiltViewModel
class TestViewModel @Inject constructor(
    private val getStoreDataUseCase: GetStoreDataUseCase,
    private val getWeatherUseCase: GetWeatherDataUseCase
) : ViewModel() {
    private val _storeData = MutableLiveData<List<StoreEntity?>?>()
    val storeData: LiveData<List<StoreEntity?>?> = _storeData

    fun fetchStoreData(lat: String, lng: String) {
        viewModelScope.launch {
            try {
                val storeDataList = getStoreDataUseCase(lat, lng)
                _storeData.value = storeDataList
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }
}