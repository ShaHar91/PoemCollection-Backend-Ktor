package com.poemcollection.data.dto.requests.review

import com.google.gson.annotations.SerializedName
import com.poemcollection.data.dto.requests.user.UserDto
import com.poemcollection.domain.models.interfaces.DateAble

data class ReviewDto(
    val id: Int = 0,
    val body: String = "",
    val rating: Int = 0,
    val user: UserDto = UserDto(),
    @SerializedName("created_at")
    override val createdAt: String = "",
    @SerializedName("updated_at")
    override val updatedAt: String = ""
) : DateAble
