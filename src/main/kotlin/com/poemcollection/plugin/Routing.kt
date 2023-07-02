package com.poemcollection.plugin

import com.poemcollection.modules.auth.authRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {

        authRouting()

        route("api/v1/") {

        }
    }
}