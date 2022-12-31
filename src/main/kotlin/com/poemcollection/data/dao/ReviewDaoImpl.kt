package com.poemcollection.data.dao

import com.poemcollection.data.models.InsertOrUpdateReview
import com.poemcollection.data.models.Review
import com.poemcollection.domain.interfaces.IReviewDao

class ReviewDaoImpl : IReviewDao {

    override suspend fun getReview(id: Int): Review? {
        TODO("Not yet implemented")
    }

    override suspend fun getReviews(poemId: Int?): List<Review> {
        TODO("Not yet implemented")
    }

    override suspend fun insertReview(insertReview: InsertOrUpdateReview): Review? {
        TODO("Not yet implemented")
    }

    override suspend fun updateReview(id: Int, updateReview: InsertOrUpdateReview): Review? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReview(id: Int): Boolean {
        TODO("Not yet implemented")
    }
}