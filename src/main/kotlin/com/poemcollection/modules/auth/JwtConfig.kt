package com.poemcollection.modules.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.poemcollection.domain.models.user.User
import com.poemcollection.model.CredentialsResponse
import io.ktor.server.config.*
import java.util.*
import java.util.concurrent.TimeUnit

class JwtConfig(
    private val issuer: String,
    private val audience: String,
    secret: String
) : TokenProvider {

    private val validityInMs: Long = TimeUnit.HOURS.toMillis(24) // 24h
    private val refreshValidityInMs: Long = TimeUnit.DAYS.toMillis(30) // 30 days
    private val algorithm = Algorithm.HMAC256(secret)

    companion object {
        const val TOKEN_CLAIM_USER_ID_KEY = "userId"
        const val USERS_AUDIENCE = "users"
    }

    constructor(config: ApplicationConfig, secret: String) : this(
        config.property("jwt.issuer").getString(),
        config.property("jwt.audience").getString(),
        secret
    )

    override val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaimPresence(TOKEN_CLAIM_USER_ID_KEY)
        .build()

    private fun createToken(user: User, expiration: Date) = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim(TOKEN_CLAIM_USER_ID_KEY, user.id)
        .withClaim("email", user.email)
        .withExpiresAt(expiration)
        .sign(algorithm)


    override fun createTokens(user: User): CredentialsResponse = CredentialsResponse(
        createToken(user, getTokenExpiration()),
        createToken(user, getTokenExpiration(refreshValidityInMs)),
        validityInMs
    )

    override fun verifyToken(token: String): Int? {
        return verifier.verify(token).claims[TOKEN_CLAIM_USER_ID_KEY]?.asInt()
    }

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getTokenExpiration(validity: Long = validityInMs) = Date(System.currentTimeMillis() + validity)
}

interface TokenProvider {
    fun createTokens(user: User): CredentialsResponse
    fun verifyToken(token: String): Int?

    // This should not be here, another tokenProvider might not use any JWT...
    // Need to find another fix for this
    val verifier: JWTVerifier
}