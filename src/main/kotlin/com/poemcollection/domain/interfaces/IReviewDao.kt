package com.poemcollection.domain.interfaces

import com.poemcollection.domain.models.Ratings
import com.poemcollection.data.dto.requests.review.InsertOrUpdateReview
import com.poemcollection.domain.models.review.Review

interface IReviewDao {

    fun getReview(id: Int): Review?
    fun getReviews(poemId: Int?, limit: Int? = null): List<Review>
    fun insertReview(poemId: Int, insertReview: InsertOrUpdateReview): Review?

    fun updateReview(id: Int, updateReview: InsertOrUpdateReview): Review?
    fun deleteReview(id: Int): Boolean
    fun calculateRatings(poemId: Int): Ratings
    fun isUserWriter(reviewId: Int, userId: Int): Boolean
}