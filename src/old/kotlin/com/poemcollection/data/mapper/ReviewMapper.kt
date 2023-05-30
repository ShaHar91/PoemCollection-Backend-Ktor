package com.poemcollection.data.mapper

import com.poemcollection.data.remote.incoming.review.InsertOrUpdateReviewDto
import com.poemcollection.data.remote.outgoing.ReviewDto
import com.poemcollection.domain.models.review.InsertOrUpdateReview
import com.poemcollection.domain.models.review.Review


fun InsertOrUpdateReviewDto.toInsertOrUpdateReview(userId: Int) = InsertOrUpdateReview(
    body,
    rating,
    userId
)

fun Review.toReviewDto() = ReviewDto(
    id,
    body,
    rating,
    user.toUserDto(),
    createdAt,
    updatedAt
)