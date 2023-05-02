package com.poemcollection.routes

import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.InsertOrUpdateReview
import com.poemcollection.routes.ParamConstants.POEM_ID_KEY
import com.poemcollection.routes.interfaces.IReviewRoutes
import com.poemcollection.security.security.token.TokenClaim
import com.poemcollection.utils.getPoemId
import com.poemcollection.utils.getReviewId
import com.poemcollection.utils.getUserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class ReviewRoutesImpl(
    private val userDao: IUserDao,
    private val reviewDao: IReviewDao
) : IReviewRoutes {

    override suspend fun postReview(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)

        val poemId = call.getPoemId() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        //TODO: have dto object for incoming requests without exposing the userId and have another model that has the userId which we will set ourselves
        val insertReview = call.receiveNullable<InsertOrUpdateReview>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        val newReview = reviewDao.insertReview(poemId, insertReview.copy(userId = userId?.toIntOrNull() ?: 0))

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
        val reviewId = call.getReviewId() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val review = reviewDao.getReview(reviewId)

        if (review != null) {
            call.respond(HttpStatusCode.OK, review)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun updateReview(call: ApplicationCall) {
        val reviewId = call.getReviewId() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        // TODO: should return a better error!!
        val userId = call.getUserId() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = reviewDao.isUserWriter(reviewId, userId)

        if (!isUserWriter && !isUserAdmin) return call.respondText("You don't have the right permissions to update this review.", status = HttpStatusCode.BadRequest)

        val updateReview = call.receive<InsertOrUpdateReview>()

        val review = reviewDao.updateReview(reviewId, updateReview)

        if (review != null) {
            call.respond(HttpStatusCode.OK, review)
        } else {
            call.respondText("Not updated", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun deleteReview(call: ApplicationCall) {
        val reviewId = call.getReviewId() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        // TODO: should return a better error!!
        val userId = call.getUserId() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = reviewDao.isUserWriter(reviewId, userId)

        if (!isUserWriter && !isUserAdmin) return call.respondText("You don't have the right permissions to update this review.", status = HttpStatusCode.BadRequest)

        val success = reviewDao.deleteReview(reviewId)

        if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
        call.respondText("Deleting!!", status = HttpStatusCode.OK)
    }
}