package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity

interface SignInWithEmailUseCase {
    suspend operator fun invoke(emailAuthEntity: EmailAuthEntity): Boolean
}