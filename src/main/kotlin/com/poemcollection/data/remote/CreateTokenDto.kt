package com.poemcollection.data.remote

@kotlinx.serialization.Serializable
data class CreateTokenDto(
    val access_token: String,
    val expires_in: Long,
    val token_type: String = "Bearer"
)
