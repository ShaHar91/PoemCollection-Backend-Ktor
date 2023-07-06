package com.poemcollection.modules.auth

import com.auth0.jwt.interfaces.JWTVerifier
import com.poemcollection.data.database.DatabaseProviderContract
import com.poemcollection.domain.interfaces.IUserDao
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*

const val adminOnly = "admin"

fun JWTAuthenticationProvider.Config.setupAuthentication(
    config: ApplicationConfig,
    jwtVerifier: JWTVerifier,
    function: suspend (JWTCredential) -> Principal?
) {
    realm = config.property("jwt.realm").getString()
    verifier(jwtVerifier)

    validate { credential ->
        function(credential)
    }
}

suspend fun JWTCredential.validateUser(databaseProvider: DatabaseProviderContract, userDao: IUserDao): Principal? {
    val userId = payload.claims[JwtConfig.TOKEN_CLAIM_USER_ID_KEY]?.asInt() ?: return null

    val user = databaseProvider.dbQuery {
        userDao.getUser(userId)
    }

    return if (payload.audience.contains(JwtConfig.USERS_AUDIENCE))
        user
    else
        null
}

suspend fun JWTCredential.validateUserIsAdmin(databaseProvider: DatabaseProviderContract, userDao: IUserDao): Principal? {
    val userId = payload.claims[JwtConfig.TOKEN_CLAIM_USER_ID_KEY]?.asInt() ?: return null

    val (isUserRoleAdmin, user) = databaseProvider.dbQuery {
        val isUserRoleAdmin = userDao.isUserRoleAdmin(userId)
        val user = userDao.getUser(userId)

        isUserRoleAdmin to user
    }

    return if (payload.audience.contains(JwtConfig.USERS_AUDIENCE) && isUserRoleAdmin)
        user
    else
        null
}

