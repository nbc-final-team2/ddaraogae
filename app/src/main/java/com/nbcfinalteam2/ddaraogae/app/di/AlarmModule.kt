package com.nbcfinalteam2.ddaraogae.app.di

import android.app.AlarmManager
import android.content.Context
import com.nbcfinalteam2.ddaraogae.domain.repository.AlarmRepository
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton
import com.nbcfinalteam2.ddaraogae.app.di.DispatcherModule.IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {

    @Provides
    @Singleton
    fun provideAlarmController(
        alarmManager: AlarmManager,
        @ApplicationContext context: Context,
        alarmRepository: AlarmRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): AlarmController = AlarmController(
        alarmManager,
        context,
        alarmRepository,
        dispatcher
    )

    @Provides
    @Singleton
    fun provideAlarmManager(
        @ApplicationContext context: Context
    ): AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}