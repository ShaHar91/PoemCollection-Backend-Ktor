package com.poemcollection.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.security.security.token.TokenClaim
import com.poemcollection.security.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {

    val config by inject<TokenConfig>()
    val userDao by inject<IUserDao>()

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
                val user = credential.payload.claims["userId"]?.asInt()?.let { userDao.getUser(it) }

                if (credential.payload.audience.contains(config.audience) && user != null) JWTPrincipal(credential.payload) else null
            }
        }

        jwt("admin") {
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT.require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .withClaimPresence(TokenClaim.TOKEN_CLAIM_USER_ID_KEY)
                    .build()
            )

            validate { credential ->
                val isUserRoleAdmin = credential.payload.claims["userId"]?.asInt()?.let { userDao.isUserRoleAdmin(it) }

                if (credential.payload.audience.contains(config.audience) && isUserRoleAdmin != null && isUserRoleAdmin) JWTPrincipal(credential.payload) else null
            }
        }
    }
}