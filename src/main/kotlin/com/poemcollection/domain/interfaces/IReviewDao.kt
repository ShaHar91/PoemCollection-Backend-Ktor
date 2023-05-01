package com.poemcollection.domain.interfaces

import com.poemcollection.domain.models.InsertOrUpdateReview
import com.poemcollection.domain.models.Ratings
import com.poemcollection.domain.models.Review

interface IReviewDao {

    suspend fun getReview(id: Int): Review?
    suspend fun getReviews(poemId: Int?): List<Review>
    suspend fun insertReview(poemId: Int, insertReview: InsertOrUpdateReview): Review?

    suspend fun updateReview(id: Int, updateReview: InsertOrUpdateReview): Review?
    suspend fun deleteReview(id: Int): Boolean
    suspend fun calculateRatings(id: Int): Ratings
    suspend fun isUserWriter(reviewId: Int, userId: Int): Boolean
}