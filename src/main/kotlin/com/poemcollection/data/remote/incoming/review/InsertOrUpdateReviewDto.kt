package com.poemcollection.data.remote.incoming.review

@kotlinx.serialization.Serializable
data class InsertOrUpdateReviewDto(
    val body: String = "",
    val rating: Int = 0
)