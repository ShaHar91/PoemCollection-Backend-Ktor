package com.poemcollection.modules.auth

import com.poemcollection.data.dto.incoming.CreateTokenDto
import com.poemcollection.utils.receiveOrRespondWithError
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRouting() {

    val authController by inject<AuthController>()

    post("oauth/token") {
        val request = call.receiveOrRespondWithError<CreateTokenDto>()
        val token = authController.authorizeUser(request)
        call.respond(token)
    }
}