package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getDogListUseCase: GetDogListUseCase,
) : ViewModel() {

    private val _dogList = MutableLiveData<List<DogEntity>>()
    val dogList: LiveData<List<DogEntity>> get() = _dogList


    init {
        loadDogs()
    }

    private fun loadDogs() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase.invoke()
            user?.let {
                _dogList.value = getDogListUseCase.invoke()
            }
        }
    }
}

