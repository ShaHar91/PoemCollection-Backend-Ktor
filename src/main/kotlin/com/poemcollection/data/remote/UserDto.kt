package com.poemcollection.data.remote

@kotlinx.serialization.Serializable
data class UserDto(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)
