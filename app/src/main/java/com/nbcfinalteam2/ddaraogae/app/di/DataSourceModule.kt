package com.nbcfinalteam2.ddaraogae.app.di

import com.nbcfinalteam2.ddaraogae.data.datasource.local.AlarmDataSource
import com.nbcfinalteam2.ddaraogae.data.datasource.local.AlarmDataSourceImpl
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.FirebaseDataSource
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.FirebaseDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseDataSource(
        firebaseDataSourceImpl: FirebaseDataSourceImpl
    ): FirebaseDataSource

    @Binds
    @Singleton
    abstract fun bindAlarmDataSource(
        alarmDataSourceImpl: AlarmDataSourceImpl
    ): AlarmDataSource
}