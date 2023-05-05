package com.poemcollection.data.remote.outgoing

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
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = ""
)
