package com.poemcollection.domain.models.user

import com.poemcollection.security.security.hashing.SaltedHash

data class UpdatePassword(
    val saltedHash: SaltedHash
)
