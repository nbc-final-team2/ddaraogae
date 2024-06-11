package com.nbcfinalteam2.ddaraogae.app.di

import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteDogUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteDogUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogByIdUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogByIdUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampNumByDogIdAndPeriodUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampNumByDogIdAndPeriodUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStoreDataUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStoreDataUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingByIdUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingByIdUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertDogUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertDogUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertStampUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertStampUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertWalkingDataUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertWalkingDataUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithEmailUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithGoogleUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithGoogleUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignOutUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignOutUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignUpWithEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignUpWithEmailUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateDogUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateDogUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindDeleteDogUseCase(
        deleteDogUseCaseImpl: DeleteDogUseCaseImpl
    ): DeleteDogUseCase

    @Binds
    abstract fun bindGetCurrentUserUseCase(
        getCurrentUserUseCaseImpl: GetCurrentUserUseCaseImpl
    ): GetCurrentUserUseCase

    @Binds
    abstract fun bindGetDogByIdUseCase(
        getDogByIdUseCaseImpl: GetDogByIdUseCaseImpl
    ): GetDogByIdUseCase

    @Binds
    abstract fun bindGetDogListUseCase(
        getDogListUseCaseImpl: GetDogListUseCaseImpl
    ): GetDogListUseCase

    @Binds
    abstract fun bindGetStampNumByDogIdAndPeriodUseCase(
        getStampNumByDogIdAndPeriodUseCaseImpl: GetStampNumByDogIdAndPeriodUseCaseImpl
    ): GetStampNumByDogIdAndPeriodUseCase

    @Binds
    abstract fun bindGetWalkingByIdUseCase(
        getWalkingByIdUseCaseImpl: GetWalkingByIdUseCaseImpl
    ): GetWalkingByIdUseCase

    @Binds
    abstract fun bindGetWalkingListByDogIdAndPeriodUseCase(
        getWalkingListByDogIdAndPeriodUseCaseImpl: GetWalkingListByDogIdAndPeriodUseCaseImpl
    ): GetWalkingListByDogIdAndPeriodUseCase

    @Binds
    abstract fun bindGetWeatherDataUseCase(
        getWeatherDataUseCaseImpl: GetWeatherDataUseCaseImpl
    ): GetWeatherDataUseCase

    @Binds
    abstract fun bindInsertDogUseCase(
        insertDogUseCaseImpl: InsertDogUseCaseImpl
    ): InsertDogUseCase

    @Binds
    abstract fun bindInsertStampUseCase(
        insertStampUseCaseImpl: InsertStampUseCaseImpl
    ): InsertStampUseCase

    @Binds
    abstract fun bindInsertWalkingDataUseCase(
        insertWalkingDataUseCaseImpl: InsertWalkingDataUseCaseImpl
    ): InsertWalkingDataUseCase

    @Binds
    abstract fun bindSignInWithEmailUseCase(
        signInWithEmailUseCaseImpl: SignInWithEmailUseCaseImpl
    ): SignInWithEmailUseCase

    @Binds
    abstract fun bindSignInWithGoogleUseCase(
        signInWithGoogleUseCaseImpl: SignInWithGoogleUseCaseImpl
    ): SignInWithGoogleUseCase

    @Binds
    abstract fun bindSignOutUseCase(
        signOutUseCaseImpl: SignOutUseCaseImpl
    ): SignOutUseCase

    @Binds
    abstract fun bindSignUpWithEmailUseCase(
        signUpWithEmailUseCaseImpl: SignUpWithEmailUseCaseImpl
    ): SignUpWithEmailUseCase

    @Binds
    abstract fun bindUpdateDogUseCase(
        updateDogUseCaseImpl: UpdateDogUseCaseImpl
    ): UpdateDogUseCase

    @Binds
    abstract fun bindGetStoreDataUseCase(
        getStoreDataUseCaseImpl: GetStoreDataUseCaseImpl
    ): GetStoreDataUseCase
}