package com.poemcollection.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class InsertNewUser(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = ""
)

@Serializable
data class UpdateUser(
    val firstName: String = "",
    val lastName: String = ""
)