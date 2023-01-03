package com.poemcollection.data.responses

@kotlinx.serialization.Serializable
data class AuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Long
)
