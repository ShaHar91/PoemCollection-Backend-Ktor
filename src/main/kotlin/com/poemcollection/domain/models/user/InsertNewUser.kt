package com.poemcollection.domain.models.user

import com.poemcollection.security.security.hashing.SaltedHash

data class InsertNewUser(
    val firstName: String,
    val lastName: String,
    val email: String,
    val saltedHash: SaltedHash
)