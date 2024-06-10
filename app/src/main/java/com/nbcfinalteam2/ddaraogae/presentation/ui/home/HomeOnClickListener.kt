package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo

interface HomeOnClickListener {
    fun onAddClick()
    fun onDogClick(dogData: DogInfo)
}