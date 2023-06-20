package com.poemcollection.domain.models.user

data class UpdateUser(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null
)

fun UpdateUser.hasData() = firstName != null || lastName != null || email != null