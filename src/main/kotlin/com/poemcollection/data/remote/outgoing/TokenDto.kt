package com.poemcollection.data.remote.outgoing

@kotlinx.serialization.Serializable
data class TokenDto(
    val access_token: String,
    val expires_in: Long,
    val token_type: String = "Bearer "
)
