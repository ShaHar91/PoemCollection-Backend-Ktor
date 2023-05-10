package com.poemcollection.routes

import com.poemcollection.data.remote.outgoing.TokenDto
import com.poemcollection.data.requests.AuthRequest
import com.poemcollection.data.responses.ErrorCodes
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.user.toSaltedHash
import com.poemcollection.routes.interfaces.IAuthRoutes
import com.poemcollection.security.security.hashing.HashingService
import com.poemcollection.security.security.token.TokenClaim
import com.poemcollection.security.security.token.TokenClaim.Companion.TOKEN_CLAIM_USER_ID_KEY
import com.poemcollection.security.security.token.TokenConfig
import com.poemcollection.security.security.token.TokenService
import com.poemcollection.utils.receiveOrRespondWithError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class AuthRoutesImpl(
    private val hashingService: HashingService,
    private val userDao: IUserDao,
    private val tokenService: TokenService,
    private val tokenConfig: TokenConfig
) : IAuthRoutes {
    override suspend fun authorizeUser(call: ApplicationCall) {
        val request = call.receiveOrRespondWithError<AuthRequest>() ?: run {
            return call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidGrant.asResponse)
        }

        val userHashable = userDao.getUserHashableByEmail(request.email)
        if (userHashable == null || !userHashable.email.contains("@"))
            return call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidCredentials.asResponse)

        val isValidPassword = hashingService.verify(request.password, userHashable.toSaltedHash())

        if (!isValidPassword)
            return call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidCredentials.asResponse)

        val token = tokenService.generate(
            tokenConfig,
            TokenClaim(TOKEN_CLAIM_USER_ID_KEY, userHashable.id)
        )

        return call.respond(HttpStatusCode.OK, TokenDto(token, tokenConfig.expiresIn))
    }
}