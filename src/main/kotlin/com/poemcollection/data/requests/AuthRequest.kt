package com.poemcollection.data.requests

@kotlinx.serialization.Serializable
data class AuthRequest(
    val email: String,
    val password: String
)
