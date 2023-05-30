package com.poemcollection.domain.models

data class SaltedHash(
    val hash: String,
    val salt: String
)
