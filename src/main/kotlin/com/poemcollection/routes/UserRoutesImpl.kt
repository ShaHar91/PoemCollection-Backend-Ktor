package com.poemcollection.routes

import com.poemcollection.data.mapper.toInsertNewUser
import com.poemcollection.data.mapper.toUserDto
import com.poemcollection.data.remote.incoming.user.InsertNewUserDto
import com.poemcollection.data.responses.ErrorCodes
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.user.UpdateUser
import com.poemcollection.routes.interfaces.IUserRoutes
import com.poemcollection.security.security.hashing.HashingService
import com.poemcollection.utils.getUserId
import com.poemcollection.utils.receiveOrRespondWithError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class UserRoutesImpl(
    private val userDao: IUserDao,
    private val hashingService: HashingService
) : IUserRoutes {

    override suspend fun postUser(call: ApplicationCall) {
        val insertNewUser = call.receiveOrRespondWithError<InsertNewUserDto>() ?: return

        if (!insertNewUser.isValid) {
            call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
            return
        }

        val userUnique = userDao.userUnique(insertNewUser.email)
        if (!userUnique || !insertNewUser.email.contains("@")) {
            call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidEmail.asResponse) // Email already exists
            return
        }

        if (!insertNewUser.isPasswordSame) {
            call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorRepeatPassword.asResponse) // repeatPassword is not the same
            return
        }

        if (!insertNewUser.isPwTooShort) {
            call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidPassword.asResponse)
            return
        }

        val saltedHash = hashingService.generateSaltedHash(insertNewUser.password)

        val newUser = userDao.insertUser(
            insertNewUser.toInsertNewUser(saltedHash)
        )

        if (newUser != null) {
            call.respond(HttpStatusCode.Created, newUser.toUserDto())
        } else {
            call.respond(HttpStatusCode.NoContent, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun getCurrentUser(call: ApplicationCall) {
        val userId = call.getUserId() ?: return

        val user = userDao.getUser(userId)

        if (user != null) {
            call.respond(HttpStatusCode.OK, user.toUserDto())
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun updateCurrentUser(call: ApplicationCall) {
        val userId = call.getUserId() ?: return

        val updateUser = call.receiveOrRespondWithError<UpdateUser>() ?: return

        if (updateUser.firstName == null && updateUser.lastName == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidBody.asResponse)
            return
        }

        val user = userDao.updateUser(userId, updateUser)

        if (user != null) {
            call.respond(HttpStatusCode.OK, user.toUserDto())
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun deleteCurrentUser(call: ApplicationCall) {
        val userId = call.getUserId() ?: return

        val success = userDao.deleteUser(userId)

        if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }
}