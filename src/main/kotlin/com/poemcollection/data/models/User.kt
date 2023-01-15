package com.poemcollection.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    @Transient val password: String = "",
    @Transient val salt: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class InsertNewUser(
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val email: String = ""
)

@Serializable
data class UpdateUser(
    val firstName: String = "",
    val lastName: String = ""
)