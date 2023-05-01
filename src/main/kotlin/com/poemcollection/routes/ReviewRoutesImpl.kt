package com.poemcollection.routes

import com.poemcollection.data.models.InsertOrUpdateReview
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.routes.ParamConstants.POEM_ID_KEY
import com.poemcollection.routes.ParamConstants.REVIEW_ID_KEY
import com.poemcollection.routes.interfaces.IReviewRoutes
import com.poemcollection.security.security.token.TokenClaim
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

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

    //TODO: only an admin or the user who created the review can edit the review
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

    //TODO: only an admin or the user who created the review can delete the review
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