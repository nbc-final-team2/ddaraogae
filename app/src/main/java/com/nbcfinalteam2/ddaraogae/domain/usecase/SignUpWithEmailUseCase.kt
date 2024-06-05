package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity

interface SignUpWithEmailUseCase {
    suspend operator fun invoke(emailAuthEntity: EmailAuthEntity): Boolean
}