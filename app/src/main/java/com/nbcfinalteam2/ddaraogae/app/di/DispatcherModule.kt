package com.nbcfinalteam2.ddaraogae.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Qualifier
    annotation class DefaultDispatcher

    @Qualifier
    annotation class IoDispatcher

    @DefaultDispatcher
    @Provides
    @Singleton
    fun provideCoroutineDefaultDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default
    }

    @IoDispatcher
    @Provides
    @Singleton
    fun provideCoroutineIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

}