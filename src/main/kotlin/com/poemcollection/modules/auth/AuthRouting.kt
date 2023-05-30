package com.poemcollection.modules.auth

import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRouting() {

    val authController by inject<AuthController>()

    post("oauth/token") {
//        val some = call.receive<Unit>()
//        val returns = authController.authorizeUser()
//        call.respond(returns)
    }
}