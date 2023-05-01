package com.poemcollection.routes

import com.poemcollection.data.responses.ErrorCodes
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.InsertNewUser
import com.poemcollection.domain.models.UpdateUser
import com.poemcollection.routes.interfaces.IUserRoutes
import com.poemcollection.security.security.hashing.HashingService
import com.poemcollection.security.security.token.TokenClaim
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

suspend inline fun <reified T> ApplicationCall.receiveOrRespondWithError(): T? {
    return try {
        receiveNullable<T>() ?: run {
            respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
            null
        }

    } catch (e: Exception) {
        respond(HttpStatusCode.InternalServerError, ErrorCodes.ErrorInvalidContentType.asResponse)
        null
    }
}

class UserRoutesImpl(
    private val userDao: IUserDao,
    private val hashingService: HashingService
) : IUserRoutes {

    override suspend fun postUser(call: ApplicationCall) {
        val user = call.receiveOrRespondWithError<InsertNewUser>() ?: return

        if (!user.isValid) {
            call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
            return
        }

        val userUnique = userDao.userUnique(user.email)
        if (!userUnique || !user.email.contains("@")) {
            call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidEmail.asResponse) // Email already exists
            return
        }

        if (!user.isPasswordSame) {
            call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorRepeatPassword.asResponse) // repeatPassword is not the same
            return
        }

        if (!user.isPwTooShort) {
            call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidPassword.asResponse)
            return
        }

        val saltedHash = hashingService.generateSaltedHash(user.password)

        val newUser = userDao.insertUser(
            user,
            saltedHash
        )

        if (newUser != null) {
            call.respond(HttpStatusCode.Created, newUser)
        } else {
            call.respondText("Not created", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getCurrentUser(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)

        val user = userDao.getUser(userId?.toIntOrNull() ?: -1)

        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun updateUserById(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)

        val updateUser = call.receive<UpdateUser>()

        val user = userDao.updateUser(userId?.toIntOrNull() ?: -1, updateUser)

        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun deleteUserById(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)

        val success = userDao.deleteUser(userId?.toIntOrNull() ?: -1)

        if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }
}