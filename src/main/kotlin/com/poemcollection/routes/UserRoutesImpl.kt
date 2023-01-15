package com.poemcollection.routes

import com.poemcollection.data.models.InsertNewUser
import com.poemcollection.data.models.UpdateUser
import com.poemcollection.data.responses.ErrorCodes
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.routes.interfaces.IUserRoutes
import com.poemcollection.security.security.hashing.HashingService
import com.poemcollection.security.security.token.TokenClaim
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class UserRoutesImpl(
    private val userDao: IUserDao,
    private val hashingService: HashingService
) : IUserRoutes {

    override suspend fun postUser(call: ApplicationCall) {
        try {
            val user = call.receiveNullable<InsertNewUser>() ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
                return
            }

            val userUnique = userDao.userUnique(user.email)
            if (!userUnique) {
                call.respond(HttpStatusCode.Conflict, ErrorCodes.UserAlreadyExistsError.asResponse) // Email already exists
            }

            val passwordSame = user.password == user.repeatPassword
            if (!passwordSame) {
                call.respond(HttpStatusCode.Conflict, "same") // repeatPassword is not the same
            }

            val isPwTooShort = user.password.length > 8
            if (!isPwTooShort) {
                call.respond(HttpStatusCode.Conflict, "too short") // Password is not strong enough, minimal of 8 characters
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
        } catch (e: Exception) {
            call.respondText("Something went wrong: ${e.message}", status = HttpStatusCode.InternalServerError)
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