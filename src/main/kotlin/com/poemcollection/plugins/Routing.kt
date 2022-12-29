package com.poemcollection.plugins

import com.poemcollection.routes.userRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {
        userRouting()
    }
}
