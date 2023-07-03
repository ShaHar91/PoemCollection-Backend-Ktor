package com.poemcollection.modules.reviews

import com.poemcollection.data.dto.requests.review.InsertOrUpdateReview
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.review.Review
import com.poemcollection.modules.BaseController
import com.poemcollection.utils.TBDException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReviewControllerImpl : BaseController(), ReviewController, KoinComponent {

    private val reviewDao by inject<IReviewDao>()
    private val userDao by inject<IUserDao>()

    override suspend fun postReview(poemId: Int, userId: Int, insertReview: InsertOrUpdateReview): Review = dbQuery {
        reviewDao.insertReview(poemId, userId, insertReview) ?: throw TBDException
    }

    override suspend fun getAllReviews(poemId: Int, limit: Int?): List<Review> = dbQuery {
        reviewDao.getReviews(poemId, limit)
    }

    override suspend fun getReviewById(reviewId: Int): Review = dbQuery {
        reviewDao.getReview(reviewId) ?: throw TBDException
    }

    override suspend fun updateReview(reviewId: Int, userId: Int, updateReview: InsertOrUpdateReview): Review = dbQuery {
        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = reviewDao.isUserWriter(reviewId, userId)

        if (!isUserWriter && !isUserAdmin) throw TBDException

        reviewDao.updateReview(reviewId, updateReview) ?: throw TBDException
    }

    override suspend fun deleteReview(userId: Int, reviewId: Int) {
        dbQuery {
            val isUserAdmin = userDao.isUserRoleAdmin(userId)
            val isUserWriter = reviewDao.isUserWriter(reviewId, userId)

            if (!isUserWriter && !isUserAdmin) throw TBDException

            reviewDao.deleteReview(reviewId)
        }
    }
}

interface ReviewController {
    suspend fun postReview(poemId: Int, userId: Int, insertReview: InsertOrUpdateReview): Review
    suspend fun getAllReviews(poemId: Int, limit: Int? = null): List<Review>
    suspend fun getReviewById(reviewId: Int): Review
    suspend fun updateReview(reviewId: Int, userId: Int, updateReview: InsertOrUpdateReview): Review
    suspend fun deleteReview(userId: Int, reviewId: Int)
}