package com.poemcollection.data.remote.outgoing

import com.poemcollection.domain.models.interfaces.DateAble
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ReviewDto(
    val id: Int = 0,
    val body: String = "",
    val rating: Int = 0,
    val user: UserDto = UserDto(),
    @SerialName("created_at")
    override val createdAt: String = "",
    @SerialName("updated_at")
    override val updatedAt: String = ""
) : DateAble