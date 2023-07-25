package com.poemcollection.controllers.reviews

import com.poemcollection.data.dto.requests.review.InsertOrUpdateReview
import com.poemcollection.domain.models.review.Review

object ReviewInstrumentation {

    fun givenAValidInsertReview() = InsertOrUpdateReview("hello", 3)
    fun givenAValidUpdateReview() = InsertOrUpdateReview("Updated hello", 4)
    fun givenAReview() = Review(1, "hello", 3)
}