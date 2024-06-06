package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCaseImpl
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getWeatherDataUseCaseImpl: GetWeatherDataUseCaseImpl
) : ViewModel() {
    private val _weatherIcon: MutableLiveData<List<WeatherEntity>> = MutableLiveData()
    val weatherIcon: LiveData<List<WeatherEntity>> get() = _weatherIcon

    fun getWeatherIcon() {
        viewModelScope.launch {

        }
    }
}