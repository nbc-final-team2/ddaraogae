package com.nbcfinalteam2.ddaraogae.presentation.ui.alarm

import androidx.lifecycle.ViewModel
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAlarmUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetAlarmListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertAlarmUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val insertAlarmUseCase: InsertAlarmUseCase,
    private val getAlarmListUseCase: GetAlarmListUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase
): ViewModel() {



}