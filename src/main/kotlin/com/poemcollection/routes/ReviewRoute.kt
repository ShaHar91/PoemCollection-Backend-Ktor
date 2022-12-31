package com.poemcollection.routes

import com.poemcollection.data.models.InsertOrUpdateReview
import com.poemcollection.domain.interfaces.IReviewDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.reviewRouting(
    reviewDao: IReviewDao
) {

    route("reviews") {
        post {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val insertReview = call.receiveNullable<InsertOrUpdateReview>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val newReview = reviewDao.insertReview(id, insertReview)

            if (newReview != null) {
                call.respond(HttpStatusCode.Created, newReview)
            } else {
                call.respondText("Not created", status = HttpStatusCode.InternalServerError)
            }
        }

        get {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val reviews = reviewDao.getReviews(id)
            call.respond(HttpStatusCode.OK, reviews)
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val review = reviewDao.getReview(id)

            if (review != null) {
                call.respond(HttpStatusCode.OK, review)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
        }

        put("{reviewId}") {
            val id = call.parameters["reviewId"]?.toIntOrNull() ?: return@put call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val updateReview = call.receive<InsertOrUpdateReview>()

            val review = reviewDao.updateReview(id, updateReview)

            if (review != null) {
                call.respond(HttpStatusCode.OK, review)
            } else {
                call.respondText("Not updated", status = HttpStatusCode.InternalServerError)
            }
        }

        delete("{reviewId}") {
            val id = call.parameters["reviewId"]?.toIntOrNull() ?: return@delete call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val success = reviewDao.deleteReview(id)

            if (success) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
            call.respondText("Deleting!!", status = HttpStatusCode.OK)
        }
    }

}