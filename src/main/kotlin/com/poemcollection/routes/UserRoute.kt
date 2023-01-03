package com.poemcollection.routes

import com.poemcollection.data.models.InsertNewUser
import com.poemcollection.data.models.UpdateUser
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.routes.ParamConstants.USER_ID_KEY
import com.poemcollection.security.security.hashing.HashingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRouting(
    userDao: IUserDao,
    hashingService: HashingService
) {

    route("/users") {
        post {

            try {
                val user = call.receiveNullable<InsertNewUser>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val isPwTooShort = user.password.length < 8
                if (isPwTooShort) {
                    call.respond(HttpStatusCode.Conflict)
                    return@post
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

        authenticate {
            get {
                val users = userDao.getUsers()

                call.respond(HttpStatusCode.OK, users)
            }


            get("{$USER_ID_KEY}}") {
                val id = call.parameters[USER_ID_KEY]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                val user = userDao.getUser(id)

                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respondText("Not found", status = HttpStatusCode.NotFound)
                }
            }

            put("{$USER_ID_KEY}") {
                val id = call.parameters[USER_ID_KEY]?.toIntOrNull() ?: return@put call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                val updateUser = call.receive<UpdateUser>()

                val user = userDao.updateUser(id, updateUser)

                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respondText("Not found", status = HttpStatusCode.NotFound)
                }
            }

            delete("{$USER_ID_KEY}") {
                val id = call.parameters[USER_ID_KEY]?.toIntOrNull() ?: return@delete call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                val success = userDao.deleteUser(id)

                if (success) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respondText("Not found", status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}