package com.nbcfinalteam2.ddaraogae.app.di

import com.nbcfinalteam2.ddaraogae.presentation.shared.SharedEventViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedEventViewModelModule {

    @Provides
    @Singleton
    fun provideSharedEventViewModel(): SharedEventViewModel {
        return SharedEventViewModel()
    }

}