package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import androidx.lifecycle.ViewModel
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampInfoListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampListByPeriodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StampDetailViewModel @Inject constructor(
    private val getStampListByPeriodUseCase: GetStampListByPeriodUseCase,
    private val getStampInfoListUseCase: GetStampInfoListUseCase
) : ViewModel() {

}