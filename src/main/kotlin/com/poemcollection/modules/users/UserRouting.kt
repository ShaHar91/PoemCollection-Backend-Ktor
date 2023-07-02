package com.poemcollection.modules.users

import com.poemcollection.ParamConstants
import com.poemcollection.domain.models.user.InsertNewUser
import com.poemcollection.domain.models.user.UpdatePassword
import com.poemcollection.domain.models.user.UpdateUser
import com.poemcollection.utils.getUserId
import com.poemcollection.utils.receiveOrRespondWithError
import com.poemcollection.utils.sendOk
import com.poemcollection.utils.user
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
            call.respond(user)
        }

        authenticate {
            get("me") {
                call.respond(call.user)
            }

            put("me") {
                val updateUser = call.receiveOrRespondWithError<UpdateUser>()
                val user = userController.deleteUserById(call.user.id, updateUser)
                call.respond(user)
            }

            put("me/password") {
                val updatePassword = call.receiveOrRespondWithError<UpdatePassword>()
                val user = userController.updateUserPasswordById(call.user.id, updatePassword)
                call.respond(user)
            }

            delete("me") {
                userController.deleteUserById(call.user.id)
                sendOk()
            }
        }

        authenticate("admin") {
            get("{${ParamConstants.USER_ID_KEY}}") {
                val userId = call.getUserId()
                val user = userController.getUserById(userId)
                call.respond(user)
            }

            put("{${ParamConstants.USER_ID_KEY}}") {
                val userId = call.getUserId()
                val updateUser = call.receiveOrRespondWithError<UpdateUser>()
                val user = userController.deleteUserById(userId, updateUser)
                call.respond(user)
            }

            put("{${ParamConstants.USER_ID_KEY}}/password") {
                val userId = call.getUserId()
                val updatePassword = call.receiveOrRespondWithError<UpdatePassword>()
                val user = userController.updateUserPasswordById(userId, updatePassword)
                call.respond(user)
            }

            delete("{${ParamConstants.USER_ID_KEY}}") {
                val userId = call.getUserId()
                userController.deleteUserById(userId)
                sendOk()
            }
        }
    }
}