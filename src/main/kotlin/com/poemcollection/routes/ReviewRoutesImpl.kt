package com.poemcollection.routes

import com.poemcollection.data.mapper.toInsertOrUpdateReview
import com.poemcollection.data.mapper.toReviewDto
import com.poemcollection.data.remote.incoming.review.InsertOrUpdateReviewDto
import com.poemcollection.data.responses.ErrorCodes
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.review.InsertOrUpdateReview
import com.poemcollection.routes.interfaces.IReviewRoutes
import com.poemcollection.utils.getPoemId
import com.poemcollection.utils.getUserId
import com.poemcollection.utils.receiveOrRespondWithError
import com.poemcollection.utils.reviewId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class ReviewRoutesImpl(
    private val userDao: IUserDao,
    private val reviewDao: IReviewDao
) : IReviewRoutes {

    override suspend fun postReview(call: ApplicationCall) {
        val userId = call.getUserId() ?: return
        val poemId = call.getPoemId() ?: return

        val insertReview = call.receiveOrRespondWithError<InsertOrUpdateReviewDto>() ?: return

        val newReview = reviewDao.insertReview(poemId, insertReview.toInsertOrUpdateReview(userId))?.toReviewDto()

        return if (newReview != null) {
            call.respond(HttpStatusCode.Created, newReview)
        } else {
            call.respond(HttpStatusCode.NoContent, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun getAllReviews(call: ApplicationCall) {
        val id = call.getPoemId() ?: return
        val limit = call.request.queryParameters[ParamConstants.LIMIT_KEY]?.toIntOrNull() // null is allowed!

        val reviews = reviewDao.getReviews(id, limit).map { it.toReviewDto() }
        return call.respond(HttpStatusCode.OK, reviews)
    }

    override suspend fun getReviewById(call: ApplicationCall) {
        val reviewId = call.reviewId() ?: return

        val review = reviewDao.getReview(reviewId)?.toReviewDto()

        return if (review != null) {
            call.respond(HttpStatusCode.OK, review)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun updateReview(call: ApplicationCall) {
        val reviewId = call.reviewId() ?: return
        val userId = call.getUserId() ?: return

        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = reviewDao.isUserWriter(reviewId, userId)

        if (!isUserWriter && !isUserAdmin) return call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidPermissionsToUpdateReview.asResponse)

        val updateReview = call.receiveOrRespondWithError<InsertOrUpdateReview>() ?: return

        val review = reviewDao.updateReview(reviewId, updateReview)?.toReviewDto()

        return if (review != null) {
            call.respond(HttpStatusCode.OK, review)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun deleteReview(call: ApplicationCall) {
        val reviewId = call.reviewId() ?: return
        val userId = call.getUserId() ?: return

        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = reviewDao.isUserWriter(reviewId, userId)

        if (!isUserWriter && !isUserAdmin) return call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidPermissionsToDeleteReview.asResponse)

        val success = reviewDao.deleteReview(reviewId)

        return if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }
}