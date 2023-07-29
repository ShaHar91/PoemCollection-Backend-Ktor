package com.poemcollection.modules.reviews

import com.poemcollection.ParamConstants
import com.poemcollection.data.dto.requests.review.InsertOrUpdateReview
import com.poemcollection.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.reviewRouting() {

    val reviewController by inject<ReviewController>()

    route("poems/{${ParamConstants.POEM_ID_KEY}}/reviews") {
        authenticate {
            post {
                val poemId = call.getPoemId()
                val userId = call.authenticatedUser.id
                val insertPoem = call.receiveOrRespondWithError<InsertOrUpdateReview>()
                val review = reviewController.postReview(poemId, userId, insertPoem)
                call.respond(HttpStatusCode.Created, review)
            }
        }

        get {
            val poemId = call.getPoemId()
            val limit = call.parameters[ParamConstants.LIMIT_KEY]?.toIntOrNull()
            val reviews = reviewController.getAllReviews(poemId, limit)
            call.respond(reviews)
        }

        route("{${ParamConstants.REVIEW_ID_KEY}}") {

            get {
                val reviewId = call.getReviewId()
                val review = reviewController.getReviewById(reviewId)
                call.respond(review)
            }

            authenticate {
                put {
                    val reviewId = call.getReviewId()
                    val userId = call.authenticatedUser.id
                    val insertPoem = call.receiveOrRespondWithError<InsertOrUpdateReview>()
                    val review = reviewController.updateReview(reviewId, userId, insertPoem)
                    call.respond(review)
                }

                delete {
                    val reviewId = call.getReviewId()
                    val userId = call.authenticatedUser.id
                    reviewController.deleteReview(userId, reviewId)
                    sendOk()
                }
            }
        }
    }
}