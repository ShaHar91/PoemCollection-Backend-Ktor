package com.poemcollection.modules.users

import com.poemcollection.ParamConstants
import com.poemcollection.data.dto.requests.user.InsertNewUser
import com.poemcollection.data.dto.requests.user.UpdatePassword
import com.poemcollection.data.dto.requests.user.UpdateUser
import com.poemcollection.domain.models.user.toDto
import com.poemcollection.utils.authenticatedUser
import com.poemcollection.utils.getUserId
import com.poemcollection.utils.receiveOrRespondWithError
import com.poemcollection.utils.sendOk
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

//TODO: use DTO for outgoing data, todo everywhere!!!
fun Route.userRouting() {

    val userController by inject<UserController>()

    route("users") {
        post("register") {
            val insertNewUser = call.receiveOrRespondWithError<InsertNewUser>()
            val user = userController.postUser(insertNewUser)
            call.respond(user.toDto())
        }

        authenticate {
            get("me") {
                call.respond(call.authenticatedUser.toDto())
            }

            put("me") {
                val updateUser = call.receiveOrRespondWithError<UpdateUser>()
                val user = userController.deleteUserById(call.authenticatedUser.id, updateUser)
                call.respond(user.toDto())
            }

            put("me/password") {
                val updatePassword = call.receiveOrRespondWithError<UpdatePassword>()
                val user = userController.updateUserPasswordById(call.authenticatedUser.id, updatePassword)
                call.respond(user.toDto())
            }

            delete("me") {
                userController.deleteUserById(call.authenticatedUser.id)
                sendOk()
            }
        }

        authenticate("admin") {
            get("{${ParamConstants.USER_ID_KEY}}") {
                val userId = call.getUserId()
                val user = userController.getUserById(userId)
                call.respond(user.toDto())
            }

            put("{${ParamConstants.USER_ID_KEY}}") {
                val userId = call.getUserId()
                val updateUser = call.receiveOrRespondWithError<UpdateUser>()
                val user = userController.deleteUserById(userId, updateUser)
                call.respond(user.toDto())
            }

            put("{${ParamConstants.USER_ID_KEY}}/password") {
                val userId = call.getUserId()
                val updatePassword = call.receiveOrRespondWithError<UpdatePassword>()
                val user = userController.updateUserPasswordById(userId, updatePassword)
                call.respond(user.toDto())
            }

            delete("{${ParamConstants.USER_ID_KEY}}") {
                val userId = call.getUserId()
                userController.deleteUserById(userId)
                sendOk()
            }
        }
    }
}