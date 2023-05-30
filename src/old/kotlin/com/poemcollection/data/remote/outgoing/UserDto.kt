package com.poemcollection.data.remote.outgoing

import com.poemcollection.domain.models.interfaces.DateAble
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserDto(
    val id: Int = 0,
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("last_name")
    val lastName: String = "",
    val email: String = "",
    @SerialName("created_at")
    override val createdAt: String = "",
    @SerialName("updated_at")
    override val updatedAt: String = ""
) : DateAble