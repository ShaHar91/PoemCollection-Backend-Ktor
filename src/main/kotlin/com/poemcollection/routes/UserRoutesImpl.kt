package com.poemcollection.routes

import com.poemcollection.data.mapper.toInsertNewUser
import com.poemcollection.data.mapper.toUpdateUser
import com.poemcollection.data.mapper.toUserDto
import com.poemcollection.data.remote.incoming.user.InsertNewUserDto
import com.poemcollection.data.remote.incoming.user.UpdatePasswordDto
import com.poemcollection.data.remote.incoming.user.UpdateUserDto
import com.poemcollection.data.responses.ErrorCodes
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.user.toSaltedHash
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

        if (!insertNewUser.isValid)
            return call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)

        val userUnique = userDao.userUnique(insertNewUser.email)
        if (!userUnique || !insertNewUser.email.contains("@"))
            return call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidEmail.asResponse)

        if (!insertNewUser.isPasswordSame)
            return call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorRepeatPassword.asResponse)

        if (!insertNewUser.isPwTooShort)
            return call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidPassword.asResponse)

        val saltedHash = hashingService.generateSaltedHash(insertNewUser.password)

        val newUser = userDao.insertUser(
            insertNewUser.toInsertNewUser(saltedHash)
        )

        return if (newUser != null) {
            call.respond(HttpStatusCode.Created, newUser.toUserDto())
        } else {
            call.respond(HttpStatusCode.NoContent, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun getCurrentUser(call: ApplicationCall) {
        val userId = call.getUserId() ?: return

        val user = userDao.getUser(userId)

        return if (user != null) {
            call.respond(HttpStatusCode.OK, user.toUserDto())
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun updateCurrentUser(call: ApplicationCall) {
        val userId = call.getUserId() ?: return

        val updateUser = call.receiveOrRespondWithError<UpdateUserDto>() ?: return

        if (updateUser.firstName == null && updateUser.lastName == null)
            return call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidBody.asResponse)

        val user = userDao.updateUser(userId, updateUser.toUpdateUser())

        return if (user != null) {
            call.respond(HttpStatusCode.OK, user.toUserDto())
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun updateCurrentUserPassword(call: ApplicationCall) {
        val userId = call.getUserId() ?: return

        val updateUserPassword = call.receiveOrRespondWithError<UpdatePasswordDto>() ?: return
        val userHashable = userDao.getUserHashableById(userId) ?: return //TODO: return a decent error stating that the user does not exist

        if (listOf(updateUserPassword.password, updateUserPassword.repeatPassword).any { it == updateUserPassword.oldPassword })
            return call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidNewPassword.asResponse)

        val isValidPassword = hashingService.verify(updateUserPassword.oldPassword, userHashable.toSaltedHash())
        if (!isValidPassword)
            return call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorIncorrectPassword.asResponse)

        if (!updateUserPassword.isPasswordSame)
            return call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorRepeatPassword.asResponse)

        if (!updateUserPassword.isPwTooShort)
            return call.respond(HttpStatusCode.Conflict, ErrorCodes.ErrorInvalidPassword.asResponse)

        val saltedHash = hashingService.generateSaltedHash(updateUserPassword.password)

        val updatedUser = userDao.updateUserPassword(userId, saltedHash)

        return if (updatedUser != null) {
            call.respond(HttpStatusCode.OK, updatedUser.toUserDto())
        } else {
            call.respond(HttpStatusCode.NoContent, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun deleteCurrentUser(call: ApplicationCall) {
        val userId = call.getUserId() ?: return

        val success = userDao.deleteUser(userId)

        return if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }
}