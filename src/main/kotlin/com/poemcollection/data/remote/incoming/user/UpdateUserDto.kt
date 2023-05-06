package com.poemcollection.data.remote.incoming.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserDto(
    val firstName: String? = null,
    val lastName: String? = null
)