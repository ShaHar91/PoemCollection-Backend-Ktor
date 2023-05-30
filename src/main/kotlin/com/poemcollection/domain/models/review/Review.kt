package com.poemcollection.domain.models.review

import com.poemcollection.domain.models.interfaces.DateAble
import com.poemcollection.domain.models.user.User

data class Review(
    val id: Int = 0,
    val body: String = "",
    val rating: Int = 0,
    val user: User = User(),
    override val createdAt: String = "",
    override val updatedAt: String = ""
) : DateAble