package com.poemcollection.routes

import com.poemcollection.data.models.InsertNewUser
import com.poemcollection.data.models.UpdateUser
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.routes.ParamConstants.USER_ID_KEY
import com.poemcollection.security.security.hashing.HashingService
import com.poemcollection.security.security.token.TokenClaim
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRouting(
    userRoutes: IUserRoutes
) {

    route("/users") {
        post { userRoutes.postUser(call) }

        authenticate {
            get { userRoutes.getAllUsers(call) }

            get("me") { userRoutes.getCurrentUser(call) }

            get("{$USER_ID_KEY}") { userRoutes.getUserById(call) }

            put("{$USER_ID_KEY}") { userRoutes.updateUserById(call) }

            delete("{$USER_ID_KEY}") { userRoutes.deleteUserById(call) }
        }
    }
}

interface IUserRoutes {
    suspend fun postUser(call: ApplicationCall)
    suspend fun getAllUsers(call: ApplicationCall)
    suspend fun getCurrentUser(call: ApplicationCall)
    suspend fun getUserById(call: ApplicationCall)
    suspend fun updateUserById(call: ApplicationCall)
    suspend fun deleteUserById(call: ApplicationCall)
}

class UserRouteImpl(
    private val userDao: IUserDao,
    private val hashingService: HashingService
) : IUserRoutes {

    override suspend fun postUser(call: ApplicationCall) {
        try {
            val user = call.receiveNullable<InsertNewUser>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return
            }

            val isPwTooShort = user.password.length < 8
            if (isPwTooShort) {
                call.respond(HttpStatusCode.Conflict)
                return
            }

            val saltedHash = hashingService.generateSaltedHash(user.password)

            val newUser = userDao.insertUser(
                user.copy(
                    password = saltedHash.hash,
                    salt = saltedHash.salt
                )
            )

            if (newUser != null) {
                call.respond(HttpStatusCode.Created, newUser)
            } else {
                call.respondText("Not created", status = HttpStatusCode.InternalServerError)
            }
        } catch (e: Exception) {
            call.respondText("Something went wrong ${e.message}", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getAllUsers(call: ApplicationCall) {
        val users = userDao.getUsers()

        call.respond(HttpStatusCode.OK, users)
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

    override suspend fun getUserById(call: ApplicationCall) {
        val id = call.parameters[USER_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val user = userDao.getUser(id)

        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun updateUserById(call: ApplicationCall) {
        val id = call.parameters[USER_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val updateUser = call.receive<UpdateUser>()

        val user = userDao.updateUser(id, updateUser)

        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun deleteUserById(call: ApplicationCall) {
        val id = call.parameters[USER_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val success = userDao.deleteUser(id)

        if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }
}