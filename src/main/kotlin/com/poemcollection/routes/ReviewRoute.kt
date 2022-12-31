package com.poemcollection.routes

import com.poemcollection.domain.interfaces.IReviewDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.reviewRouting(
    reviewDao: IReviewDao
) {

    route("reviews") {
        post {
            // TODO: check if the poemId does exist!!!


            call.respondText("Posting!!", status = HttpStatusCode.OK)
        }

        get {
            call.respondText("Get All!!", status = HttpStatusCode.OK)
        }

        get("{id}") {
            call.respondText("Get by id", status = HttpStatusCode.OK)
        }

        put("{id}") {
            call.respondText("Updating!!", status = HttpStatusCode.OK)
        }

        delete("{id}") {
            call.respondText("Deleting!!", status = HttpStatusCode.OK)
        }
    }

}