package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertDogUseCase

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getDogListUseCase: GetDogListUseCase,
    private val insertDogUseCase: InsertDogUseCase
) : ViewModel() {

    private val _dogList = MutableLiveData<List<DogEntity>>()
    val dogList: LiveData<List<DogEntity>> get() = _dogList

}

