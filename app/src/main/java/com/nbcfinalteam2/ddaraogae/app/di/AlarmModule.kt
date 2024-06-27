package com.nbcfinalteam2.ddaraogae.app.di

import android.app.AlarmManager
import android.content.Context
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {

    @Provides
    @Singleton
    fun provideAlarmController(
        alarmManager: AlarmManager,
        @ApplicationContext context: Context
    ): AlarmController = AlarmController(
        alarmManager,
        context
    )

    @Provides
    @Singleton
    fun provideAlarmManager(
        @ApplicationContext context: Context
    ): AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}