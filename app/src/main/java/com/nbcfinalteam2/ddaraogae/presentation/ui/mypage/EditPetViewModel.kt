package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogByIdUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertDogUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateDogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditPetViewModel @Inject constructor(
    private val insertDogUseCase: InsertDogUseCase,
    private val updateDogUseCase: UpdateDogUseCase,
    private val getDogByIdUseCase: GetDogByIdUseCase
) : ViewModel() {

}