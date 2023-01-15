package com.poemcollection.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.poemcollection.security.security.token.TokenClaim
import com.poemcollection.security.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {

    val config by inject<TokenConfig>()

    authentication {
        jwt {
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT.require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .withClaimPresence(TokenClaim.TOKEN_CLAIM_USER_ID_KEY)
                    .build()
            )

            validate { credential ->
                if (credential.payload.audience.contains(config.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}