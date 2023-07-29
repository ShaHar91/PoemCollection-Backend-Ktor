package com.poemcollection.modules.auth

import com.poemcollection.data.dto.requests.CreateTokenDto
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.model.CredentialsResponse
import com.poemcollection.modules.BaseController
import com.poemcollection.statuspages.ErrorInvalidCredentials
import com.poemcollection.statuspages.ErrorInvalidParameters
import com.poemcollection.statuspages.ErrorNotFound
import com.poemcollection.utils.PasswordManagerContract
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthControllerImpl : BaseController(), AuthController, KoinComponent {

    private val userDao by inject<IUserDao>()
    private val tokenProvider by inject<TokenProvider>()
    private val passwordManager by inject<PasswordManagerContract>()

    override suspend fun authorizeUser(tokenDto: CreateTokenDto): CredentialsResponse = dbQuery {

        if (!tokenDto.email.contains("@")) throw ErrorInvalidParameters

        val userHashable = userDao.getUserHashableByEmail(tokenDto.email) ?: throw ErrorNotFound

        val isValidPassword = passwordManager.validatePassword(tokenDto.password, userHashable.password ?: "")
        if (isValidPassword) {
            tokenProvider.createTokens(userHashable.copy(password = null))
        } else {
            throw ErrorInvalidCredentials
        }
    }
}

interface AuthController {
    suspend fun authorizeUser(tokenDto: CreateTokenDto): CredentialsResponse
}