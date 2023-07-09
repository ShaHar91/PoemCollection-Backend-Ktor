package com.poemcollection.modules.users

import com.poemcollection.ParamConstants
import com.poemcollection.data.dto.requests.user.InsertNewUser
import com.poemcollection.data.dto.requests.user.UpdatePassword
import com.poemcollection.data.dto.requests.user.UpdateUser
import com.poemcollection.domain.models.user.toDto
import com.poemcollection.modules.auth.adminOnly
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
            call.respond(user)
        }

        authenticate {
            route("me") {
                get {
                    call.respond(call.authenticatedUser.toDto())
                }

                put {
                    val updateUser = call.receiveOrRespondWithError<UpdateUser>()
                    val user = userController.updateUserById(call.authenticatedUser.id, updateUser)
                    call.respond(user)
                }

                put("password") {
                    val updatePassword = call.receiveOrRespondWithError<UpdatePassword>()
                    val user = userController.updateUserPasswordById(call.authenticatedUser.id, updatePassword)
                    call.respond(user)
                }

                delete {
                    userController.deleteUserById(call.authenticatedUser.id)
                    sendOk()
                }
            }
        }

        authenticate(adminOnly) {
            route("{${ParamConstants.USER_ID_KEY}}") {
                get {
                    val userId = call.getUserId()
                    val user = userController.getUserById(userId)
                    call.respond(user)
                }

                put {
                    val userId = call.getUserId()
                    val updateUser = call.receiveOrRespondWithError<UpdateUser>()
                    val user = userController.updateUserById(userId, updateUser)
                    call.respond(user)
                }

                put("/password") {
                    val userId = call.getUserId()
                    val updatePassword = call.receiveOrRespondWithError<UpdatePassword>()
                    val user = userController.updateUserPasswordById(userId, updatePassword)
                    call.respond(user)
                }

                delete {
                    val userId = call.getUserId()
                    userController.deleteUserById(userId)
                    sendOk()
                }
            }
        }
    }
}