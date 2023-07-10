package com.poemcollection.domain.models.review

import com.poemcollection.data.dto.requests.review.ReviewDto
import com.poemcollection.domain.models.interfaces.DateAble
import com.poemcollection.domain.models.user.User
import com.poemcollection.domain.models.user.toDto

data class Review(
    val id: Int = 0,
    val body: String = "",
    val rating: Int = 0,
    val user: User = User(),
    override val createdAt: String = "",
    override val updatedAt: String = ""
) : DateAble

fun Review.toDto() = ReviewDto(
    this.id,
    this.body,
    this.rating,
    this.user.toDto(),
    this.createdAt,
    this.updatedAt
)