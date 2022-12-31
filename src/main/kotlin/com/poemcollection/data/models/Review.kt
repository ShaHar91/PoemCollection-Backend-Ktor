package com.poemcollection.data.models

@kotlinx.serialization.Serializable
data class Review(
    val id: Int = 0,
    val body: String = "",
    val rating: Int = 0,
    val user: User = User(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

@kotlinx.serialization.Serializable
data class InsertOrUpdateReview(
    val body: String = "",
    val rating: Int = 0,
    val userId: Int = 0
)