package com.poemcollection.routes

import com.poemcollection.data.requests.AuthRequest
import com.poemcollection.data.responses.AuthResponse
import com.poemcollection.domain.interfaces.IUserDao
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
import io.ktor.server.routing.*

fun Route.authRouting(
    authRoutes: IAuthRoutes
) {

//    route("/oauth") {
    post("oauth/token") {
        authRoutes.authorizeUser(call)
    }
//    }
}

interface IAuthRoutes {
    suspend fun authorizeUser(call: ApplicationCall)
}

class AuthRoutesImpl(
    private val hashingService: HashingService,
    private val userDao: IUserDao,
    private val tokenService: TokenService,
    private val tokenConfig: TokenConfig
) : IAuthRoutes {
    override suspend fun authorizeUser(call: ApplicationCall) {
        val request = call.receiveNullable<AuthRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        val user = userDao.getUserByEmail(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict)
            return
        }

        val isValidPassword = hashingService.verify(request.password, SaltedHash(user.password, user.salt))

        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict)
            return
        }

        val token = tokenService.generate(
            tokenConfig,
            TokenClaim(TOKEN_CLAIM_USER_ID_KEY, user.id)
        )

        call.respond(HttpStatusCode.OK, AuthResponse(token, "Bearer", tokenConfig.expiresIn))
    }
}