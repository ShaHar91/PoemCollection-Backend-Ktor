package com.poemcollection.data.remote.incoming

@kotlinx.serialization.Serializable
data class CreateTokenDto(
    val email: String,
    val password: String
)
