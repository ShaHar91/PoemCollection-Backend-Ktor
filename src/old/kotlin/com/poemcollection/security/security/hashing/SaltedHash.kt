package com.poemcollection.security.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)
