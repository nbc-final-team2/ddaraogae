package com.nbcfinalteam2.ddaraogae.app.di

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object DeviceStateManagerModule {

    @Provides
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    fun provideLocationManager(
        @ApplicationContext context: Context
    ): LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
}