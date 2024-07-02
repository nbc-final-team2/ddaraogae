package com.nbcfinalteam2.ddaraogae.app.di

import com.nbcfinalteam2.ddaraogae.domain.usecase.CheckStampConditionUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.CheckStampConditionUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAccountUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAccountUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAlarmUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAlarmUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteDogUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteDogUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetAlarmListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetAlarmListUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogByIdUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogByIdUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampInfoListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampInfoListUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampListByPeriodUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampListByPeriodUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampNumByPeriodUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampNumByPeriodUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStoreDataUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStoreDataUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingByIdUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingByIdUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertAlarmUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertAlarmUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertDogUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertDogUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertStampUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertStampUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertWalkingDataUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertWalkingDataUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.IsCurrentUserEmailVerifiedUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.IsCurrentUserEmailVerifiedUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.IsGoogleUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.IsGoogleUserUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.SendVerificationEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SendVerificationEmailUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithEmailUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithGoogleUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithGoogleUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignOutUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignOutUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignUpWithEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignUpWithEmailUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateAlarmUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateAlarmUseCaseImpl
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateDogUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateDogUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    @ViewModelScoped
    abstract fun bindDeleteDogUseCase(
        deleteDogUseCaseImpl: DeleteDogUseCaseImpl
    ): DeleteDogUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetCurrentUserUseCase(
        getCurrentUserUseCaseImpl: GetCurrentUserUseCaseImpl
    ): GetCurrentUserUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetDogByIdUseCase(
        getDogByIdUseCaseImpl: GetDogByIdUseCaseImpl
    ): GetDogByIdUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetDogListUseCase(
        getDogListUseCaseImpl: GetDogListUseCaseImpl
    ): GetDogListUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetStampNumByPeriodUseCase(
        getStampNumByPeriodUseCaseImpl: GetStampNumByPeriodUseCaseImpl
    ): GetStampNumByPeriodUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetStampListByPeriodUseCase(
        getStampListByPeriodUseCaseImpl: GetStampListByPeriodUseCaseImpl
    ): GetStampListByPeriodUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetWalkingByIdUseCase(
        getWalkingByIdUseCaseImpl: GetWalkingByIdUseCaseImpl
    ): GetWalkingByIdUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetWalkingListByDogIdAndPeriodUseCase(
        getWalkingListByDogIdAndPeriodUseCaseImpl: GetWalkingListByDogIdAndPeriodUseCaseImpl
    ): GetWalkingListByDogIdAndPeriodUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetWeatherDataUseCase(
        getWeatherDataUseCaseImpl: GetWeatherDataUseCaseImpl
    ): GetWeatherDataUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindInsertDogUseCase(
        insertDogUseCaseImpl: InsertDogUseCaseImpl
    ): InsertDogUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindInsertStampUseCase(
        insertStampUseCaseImpl: InsertStampUseCaseImpl
    ): InsertStampUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindInsertWalkingDataUseCase(
        insertWalkingDataUseCaseImpl: InsertWalkingDataUseCaseImpl
    ): InsertWalkingDataUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindSignInWithEmailUseCase(
        signInWithEmailUseCaseImpl: SignInWithEmailUseCaseImpl
    ): SignInWithEmailUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindSignInWithGoogleUseCase(
        signInWithGoogleUseCaseImpl: SignInWithGoogleUseCaseImpl
    ): SignInWithGoogleUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindSignOutUseCase(
        signOutUseCaseImpl: SignOutUseCaseImpl
    ): SignOutUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindSignUpWithEmailUseCase(
        signUpWithEmailUseCaseImpl: SignUpWithEmailUseCaseImpl
    ): SignUpWithEmailUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindUpdateDogUseCase(
        updateDogUseCaseImpl: UpdateDogUseCaseImpl
    ): UpdateDogUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetStoreDataUseCase(
        getStoreDataUseCaseImpl: GetStoreDataUseCaseImpl
    ): GetStoreDataUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindIsCurrentUserEmailVerifiedUseCase(
        isCurrentUserEmailVerifiedUseCaseImpl: IsCurrentUserEmailVerifiedUseCaseImpl
    ): IsCurrentUserEmailVerifiedUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindDeleteAccountUseCase(
        deleteAccountUseCaseImpl: DeleteAccountUseCaseImpl
    ): DeleteAccountUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindSendVerificationEmailUseCase(
        sendVerificationEmailUseCaseImpl: SendVerificationEmailUseCaseImpl
    ): SendVerificationEmailUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindCheckStampConditionUseCase(
        checkStampConditionUseCaseImpl: CheckStampConditionUseCaseImpl
    ): CheckStampConditionUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetStampInfoListUseCase(
        getStampInfoListUseCaseImpl: GetStampInfoListUseCaseImpl
    ): GetStampInfoListUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindInsertAlarmUseCase(
        insertAlarmUseCaseImpl: InsertAlarmUseCaseImpl
    ): InsertAlarmUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetAlarmListUseCase(
        getAlarmListUseCaseImpl: GetAlarmListUseCaseImpl
    ): GetAlarmListUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindUpdateAlarmUseCase(
        updateAlarmUseCaseImpl: UpdateAlarmUseCaseImpl
    ): UpdateAlarmUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindDeleteAlarmUseCase(
        deleteAlarmUseCaseImpl: DeleteAlarmUseCaseImpl
    ): DeleteAlarmUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindIsGoogleUserUseCase(
        isGoogleUserUseCaseImpl: IsGoogleUserUseCaseImpl
    ): IsGoogleUserUseCase
}