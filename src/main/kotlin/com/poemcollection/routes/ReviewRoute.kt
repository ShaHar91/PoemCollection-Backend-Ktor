package com.poemcollection.routes

import com.poemcollection.data.models.InsertOrUpdateReview
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.routes.ParamConstants.POEM_ID_KEY
import com.poemcollection.routes.ParamConstants.REVIEW_ID_KEY
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
            val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return@post call.respondText("Missing id", status = HttpStatusCode.BadRequest)

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
            val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val reviews = reviewDao.getReviews(id)
            call.respond(HttpStatusCode.OK, reviews)
        }

        get("{$REVIEW_ID_KEY}") {
            val id = call.parameters[REVIEW_ID_KEY]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val review = reviewDao.getReview(id)

            if (review != null) {
                call.respond(HttpStatusCode.OK, review)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
        }

        put("{$REVIEW_ID_KEY}") {
            val id = call.parameters[REVIEW_ID_KEY]?.toIntOrNull() ?: return@put call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val updateReview = call.receive<InsertOrUpdateReview>()

            val review = reviewDao.updateReview(id, updateReview)

            if (review != null) {
                call.respond(HttpStatusCode.OK, review)
            } else {
                call.respondText("Not updated", status = HttpStatusCode.InternalServerError)
            }
        }

        delete("{$REVIEW_ID_KEY}") {
            val id = call.parameters[REVIEW_ID_KEY]?.toIntOrNull() ?: return@delete call.respondText("Missing id", status = HttpStatusCode.BadRequest)

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