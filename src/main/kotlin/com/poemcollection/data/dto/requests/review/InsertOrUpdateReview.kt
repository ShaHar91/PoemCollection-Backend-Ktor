package com.poemcollection.data.dto.requests.review

data class InsertOrUpdateReview(
    val body: String = "",
    val rating: Int = 0,
    val userId: Int = 0
)