package com.poemcollection.domain.models.user

import com.poemcollection.domain.models.SaltedHash
import kotlinx.serialization.Transient

data class UserHashable(
    val id: Int = 0,
    val email: String = "",
    // Normally this object will never be passed through as a response.
    // Just in case we set the password + salt as Transient, so they will never be handed along!
    @Transient val password: String = "",
    @Transient val salt: String = ""
)

fun UserHashable.toSaltedHash() = SaltedHash(
    password, salt
)