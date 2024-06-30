package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import androidx.lifecycle.ViewModel
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampInfoListUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampNumByPeriodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StampDetailViewModel @Inject constructor(
    private val getStampNumByPeriodUseCase: GetStampNumByPeriodUseCase,
    private val getStampInfoListUseCaseImpl: GetStampInfoListUseCaseImpl
) : ViewModel() {

}