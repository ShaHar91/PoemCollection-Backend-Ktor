package com.poemcollection.modules.auth

import com.poemcollection.modules.BaseController
import org.koin.core.component.KoinComponent

class AuthControllerImpl : BaseController(), AuthController, KoinComponent {

    override suspend fun authorizeUser(): Any {
        return Unit
    }
}

interface AuthController {
    suspend fun authorizeUser(): Any
}