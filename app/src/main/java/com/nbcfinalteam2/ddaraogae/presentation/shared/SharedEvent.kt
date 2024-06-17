package com.nbcfinalteam2.ddaraogae.presentation.shared

sealed interface SharedEvent {
    data object Occur: SharedEvent
}