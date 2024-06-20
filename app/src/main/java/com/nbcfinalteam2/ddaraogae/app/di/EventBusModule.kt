package com.nbcfinalteam2.ddaraogae.app.di

import com.nbcfinalteam2.ddaraogae.domain.bus.ItemChangedEventBus
import com.nbcfinalteam2.ddaraogae.presentation.shared.ItemChangedEventBusImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EventBusModule {

    @Binds
    @Singleton
    abstract fun bindsItemChangedEventBus(
        itemChangedEventBusImpl: ItemChangedEventBusImpl
    ): ItemChangedEventBus

}