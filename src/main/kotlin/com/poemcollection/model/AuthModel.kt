package com.poemcollection.model

data class CredentialsResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Long,
    val token_type: String = "Bearer "
)
