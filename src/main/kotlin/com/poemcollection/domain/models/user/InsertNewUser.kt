package com.poemcollection.domain.models.user

import com.poemcollection.domain.models.SaltedHash

data class InsertNewUser(
    val firstName: String,
    val lastName: String,
    val email: String,
    val saltedHash: SaltedHash
)