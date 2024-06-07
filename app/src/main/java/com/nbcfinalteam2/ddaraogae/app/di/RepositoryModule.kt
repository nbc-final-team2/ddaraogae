package com.nbcfinalteam2.ddaraogae.app.di

import com.nbcfinalteam2.ddaraogae.data.repository.AuthRepositoryImpl
import com.nbcfinalteam2.ddaraogae.data.repository.DogRepositoryImpl
import com.nbcfinalteam2.ddaraogae.data.repository.StampRepositoryImpl
import com.nbcfinalteam2.ddaraogae.data.repository.StoreRepositoryImpl
import com.nbcfinalteam2.ddaraogae.data.repository.WalkingRepositoryImpl
import com.nbcfinalteam2.ddaraogae.data.repository.WeatherRepositoryImpl
import com.nbcfinalteam2.ddaraogae.domain.repository.AuthRepository
import com.nbcfinalteam2.ddaraogae.domain.repository.DogRepository
import com.nbcfinalteam2.ddaraogae.domain.repository.StampRepository
import com.nbcfinalteam2.ddaraogae.domain.repository.StoreRepository
import com.nbcfinalteam2.ddaraogae.domain.repository.WalkingRepository
import com.nbcfinalteam2.ddaraogae.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindDogRepository(
        dogRepositoryImpl: DogRepositoryImpl
    ): DogRepository

    @Binds
    abstract fun bindStampRepository(
        stampRepositoryImpl: StampRepositoryImpl
    ): StampRepository

    @Binds
    abstract fun bindWalkingRepository(
        walkingRepositoryImpl: WalkingRepositoryImpl
    ): WalkingRepository

    @Binds
    abstract fun weatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    abstract fun storeRepository(
        storeRepositoryImpl: StoreRepositoryImpl
    ): StoreRepository
}