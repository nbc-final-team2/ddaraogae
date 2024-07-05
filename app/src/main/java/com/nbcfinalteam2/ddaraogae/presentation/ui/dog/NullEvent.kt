package com.nbcfinalteam2.ddaraogae.presentation.ui.dog

sealed interface NullEvent {
    data object NullData : NullEvent
}