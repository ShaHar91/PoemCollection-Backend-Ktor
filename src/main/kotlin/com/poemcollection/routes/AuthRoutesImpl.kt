package com.poemcollection.routes

import com.poemcollection.data.remote.CreateTokenDto
import com.poemcollection.data.requests.AuthRequest
import com.poemcollection.data.responses.ErrorCodes
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.routes.interfaces.IAuthRoutes
import com.poemcollection.security.security.hashing.HashingService
import com.poemcollection.security.security.hashing.SaltedHash
import com.poemcollection.security.security.token.TokenClaim
import com.poemcollection.security.security.token.TokenClaim.Companion.TOKEN_CLAIM_USER_ID_KEY
import com.poemcollection.security.security.token.TokenConfig
import com.poemcollection.security.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AuthRoutesImpl(
    private val hashingService: HashingService,
    private val userDao: IUserDao,
    private val tokenService: TokenService,
    private val tokenConfig: TokenConfig
) : IAuthRoutes {
    override suspend fun authorizeUser(call: ApplicationCall) {
        val request = call.receiveNullable<AuthRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidGrant.asResponse)
            return
        }

        val userHashable = userDao.getUserHashableByEmail(request.email)
        if (userHashable == null || !userHashable.email.contains("@")) {
            call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidCredentials.asResponse)
            return
        }

        val isValidPassword = hashingService.verify(request.password, SaltedHash(userHashable.password, userHashable.salt))

        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidCredentials.asResponse)
            return
        }

        val token = tokenService.generate(
            tokenConfig,
            TokenClaim(TOKEN_CLAIM_USER_ID_KEY, userHashable.id)
        )

        call.respond(HttpStatusCode.OK, CreateTokenDto(token, tokenConfig.expiresIn))
    }
}