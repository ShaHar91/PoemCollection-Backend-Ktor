package com.poemcollection.modules.reviews

import com.poemcollection.data.dto.requests.review.InsertOrUpdateReview
import com.poemcollection.data.dto.requests.review.ReviewDto
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.review.toDto
import com.poemcollection.modules.BaseController
import com.poemcollection.utils.TBDException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReviewControllerImpl : BaseController(), ReviewController, KoinComponent {

    private val reviewDao by inject<IReviewDao>()
    private val userDao by inject<IUserDao>()

    override suspend fun postReview(poemId: Int, userId: Int, insertReview: InsertOrUpdateReview): ReviewDto = dbQuery {
        reviewDao.insertReview(poemId, userId, insertReview)?.toDto() ?: throw TBDException
    }

    override suspend fun getAllReviews(poemId: Int, limit: Int?): List<ReviewDto> = dbQuery {
        reviewDao.getReviews(poemId, limit).map { it.toDto() }
    }

    override suspend fun getReviewById(reviewId: Int): ReviewDto = dbQuery {
        reviewDao.getReview(reviewId)?.toDto() ?: throw TBDException
    }

    override suspend fun updateReview(reviewId: Int, userId: Int, updateReview: InsertOrUpdateReview): ReviewDto = dbQuery {
        //TODO: do we need to check if the reviewId is available?

        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = reviewDao.isUserWriter(reviewId, userId)

        if (!isUserWriter && !isUserAdmin) throw TBDException

        reviewDao.updateReview(reviewId, updateReview)?.toDto() ?: throw TBDException
    }

    override suspend fun deleteReview(userId: Int, reviewId: Int) {
        dbQuery {
            //TODO: do we need to check if the reviewId is available?

            val isUserAdmin = userDao.isUserRoleAdmin(userId)
            val isUserWriter = reviewDao.isUserWriter(reviewId, userId)

            if (!isUserWriter && !isUserAdmin) throw TBDException

            val deleted = reviewDao.deleteReview(reviewId)
            if (!deleted) throw TBDException
        }
    }
}

interface ReviewController {
    suspend fun postReview(poemId: Int, userId: Int, insertReview: InsertOrUpdateReview): ReviewDto
    suspend fun getAllReviews(poemId: Int, limit: Int? = null): List<ReviewDto>
    suspend fun getReviewById(reviewId: Int): ReviewDto
    suspend fun updateReview(reviewId: Int, userId: Int, updateReview: InsertOrUpdateReview): ReviewDto
    suspend fun deleteReview(userId: Int, reviewId: Int)
}