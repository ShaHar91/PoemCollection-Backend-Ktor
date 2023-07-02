package com.poemcollection.domain.models.user

//import com.poemcollection.domain.models.SaltedHash

data class UpdatePassword(
    val saltedHash: String
)
