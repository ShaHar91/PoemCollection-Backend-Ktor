package com.poemcollection.plugins

import com.poemcollection.data.responses.ErrorCodes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, ErrorCodes.ErrorInternalServerIssue.asResponse)
        }

        status(HttpStatusCode.NotFound) { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }
}