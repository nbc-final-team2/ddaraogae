package com.nbcfinalteam2.ddaraogae.presentation.ui.main

import androidx.lifecycle.ViewModel
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase
): ViewModel() {

}