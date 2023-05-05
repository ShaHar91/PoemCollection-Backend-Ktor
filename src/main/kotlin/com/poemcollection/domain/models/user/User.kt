package com.poemcollection.domain.models.user

import com.poemcollection.data.UserRoles
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

data class UserHashable(
    val id: Int = 0,
    val email: String = "",
    // Normally this object will never be passed through as a response.
    // Just in case we set the password + salt as Transient, so they will never be handed along!
    @Transient val password: String = "",
    @Transient val salt: String = ""
)

@Serializable
data class User(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val role: UserRoles = UserRoles.User
)

@Serializable
data class UpdateUser(
    val firstName: String? = null,
    val lastName: String? = null
)