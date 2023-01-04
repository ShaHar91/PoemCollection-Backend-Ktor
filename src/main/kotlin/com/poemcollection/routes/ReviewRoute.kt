package com.poemcollection.routes

import com.poemcollection.data.models.InsertOrUpdateReview
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.routes.ParamConstants.POEM_ID_KEY
import com.poemcollection.routes.ParamConstants.REVIEW_ID_KEY
import com.poemcollection.security.security.token.TokenClaim
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.reviewRouting(
    reviewRoutes: IReviewRoutes
) {

    route("poems/{$POEM_ID_KEY}/reviews") {
        authenticate {
            post { reviewRoutes.postReview(call) }
        }

        get { reviewRoutes.getAllReviews(call) }

        route("{$REVIEW_ID_KEY}") {

            get { reviewRoutes.getReviewById(call) }

            authenticate {
                put { reviewRoutes.updateReview(call) }

                delete { reviewRoutes.deleteReview(call) }
            }
        }
    }
}

interface IReviewRoutes {
    suspend fun postReview(call: ApplicationCall)
    suspend fun getAllReviews(call: ApplicationCall)
    suspend fun getReviewById(call: ApplicationCall)
    suspend fun updateReview(call: ApplicationCall)
    suspend fun deleteReview(call: ApplicationCall)
}

class ReviewRoutesImpl(
    private val reviewDao: IReviewDao
) : IReviewRoutes {

    override suspend fun postReview(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)

        val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val insertReview = call.receiveNullable<InsertOrUpdateReview>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        val newReview = reviewDao.insertReview(id, insertReview.copy(userId = userId?.toIntOrNull() ?: 0))

        if (newReview != null) {
            call.respond(HttpStatusCode.Created, newReview)
        } else {
            call.respondText("Not created", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getAllReviews(call: ApplicationCall) {
        val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val reviews = reviewDao.getReviews(id)
        call.respond(HttpStatusCode.OK, reviews)
    }

    override suspend fun getReviewById(call: ApplicationCall) {
        val id = call.parameters[REVIEW_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val review = reviewDao.getReview(id)

        if (review != null) {
            call.respond(HttpStatusCode.OK, review)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun updateReview(call: ApplicationCall) {
        val id = call.parameters[REVIEW_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val updateReview = call.receive<InsertOrUpdateReview>()

        val review = reviewDao.updateReview(id, updateReview)

        if (review != null) {
            call.respond(HttpStatusCode.OK, review)
        } else {
            call.respondText("Not updated", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun deleteReview(call: ApplicationCall) {
        val id = call.parameters[REVIEW_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val success = reviewDao.deleteReview(id)

        if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
        call.respondText("Deleting!!", status = HttpStatusCode.OK)
    }
}