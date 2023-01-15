package com.poemcollection.routes

import com.poemcollection.data.requests.AuthRequest
import com.poemcollection.data.responses.AuthResponse
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
            call.respond(HttpStatusCode.BadRequest, ErrorCodes.InvalidGrantError.asResponse)
            return
        }

        val user = userDao.getUserByEmail(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, ErrorCodes.InvalidCredentialsError.asResponse)
            return
        }

        val isValidPassword = hashingService.verify(request.password, SaltedHash(user.password, user.salt))

        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, ErrorCodes.InvalidCredentialsError.asResponse)
            return
        }

        val token = tokenService.generate(
            tokenConfig,
            TokenClaim(TOKEN_CLAIM_USER_ID_KEY, user.id)
        )

        call.respond(HttpStatusCode.OK, AuthResponse(token, "Bearer", tokenConfig.expiresIn))
    }
}