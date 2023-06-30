package com.poemcollection.data.database.instrumentation

import com.poemcollection.domain.models.review.InsertOrUpdateReview

object ReviewInstrumentation {

    fun givenAValidInsertReviewBody() = InsertOrUpdateReview("review body", 2, 1)
    fun givenAValidUpdateReviewBody() = InsertOrUpdateReview("Update review body", 5, 1)
}