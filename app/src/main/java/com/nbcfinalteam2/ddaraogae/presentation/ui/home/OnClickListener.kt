package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

interface OnClickListener {
    fun onAddClick()
    fun onDogClick(dogData: DogEntity)
    fun onMonthClick(monthNumber: Int)
}