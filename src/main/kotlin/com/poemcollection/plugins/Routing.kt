package com.poemcollection.plugins

import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.routes.authRouting
import com.poemcollection.routes.categoryRouting
import com.poemcollection.routes.poemRouting
import com.poemcollection.routes.userRouting
import com.poemcollection.security.security.hashing.HashingService
import com.poemcollection.security.security.token.TokenConfig
import com.poemcollection.security.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(tokenConfig: TokenConfig) {

    routing {

        val userDao by inject<IUserDao>()
        val hashingService by inject<HashingService>()
        val tokenService by inject<TokenService>()
        authRouting(hashingService, userDao, tokenService, tokenConfig)
        userRouting(userDao, hashingService)

        val categoryDao by inject<ICategoryDao>()
        categoryRouting(categoryDao)

        val poemDao by inject<IPoemDao>()
        val reviewDao by inject<IReviewDao>()
        poemRouting(poemDao, reviewDao)
    }
}
