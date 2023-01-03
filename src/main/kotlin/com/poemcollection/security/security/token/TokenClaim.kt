package com.poemcollection.security.security.token

data class TokenClaim(
    val name: String,
    val value: Int
) {
    companion object {
        const val TOKEN_CLAIM_USER_ID_KEY = "userId"
    }
}
