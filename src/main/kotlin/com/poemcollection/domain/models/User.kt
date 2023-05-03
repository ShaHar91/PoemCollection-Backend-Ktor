package com.poemcollection.domain.models

import com.poemcollection.data.UserRoles
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

data class UserHashable(
    val id: Int = 0,
    val email: String = "",
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
data class InsertNewUser(
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val email: String = ""
) {

    val isValid get() = firstName.isNotBlank() && lastName.isNotBlank() && password.isNotBlank() && repeatPassword.isNotBlank() && email.isNotBlank()

    val isPasswordSame get() = password == repeatPassword

    // Password should at least be 8 characters long AND should contain at least 1 capital letter
    val isPwTooShort get() = password.length >= 8 && password.contains(Regex("[A-Z]"))
}

@Serializable
data class UpdateUser(
    val firstName: String? = null,
    val lastName: String? = null
)