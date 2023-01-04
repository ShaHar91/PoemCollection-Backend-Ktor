package com.poemcollection.plugins

import com.poemcollection.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    routing {
        val authRoutes by inject<IAuthRoutes>()
        authRouting(authRoutes)

        val userRoutes by inject<IUserRoutes>()
        userRouting(userRoutes)

        val categoryRoutes by inject<ICategoryRoutes>()
        categoryRouting(categoryRoutes)

        val poemRoutes by inject<IPoemRoutes>()
        poemRouting(poemRoutes)

        val reviewRoutes by inject<IReviewRoutes>()
        reviewRouting(reviewRoutes)
    }
}
