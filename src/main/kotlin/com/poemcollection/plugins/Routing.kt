package com.poemcollection.plugins

import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.routes.categoryRouting
import com.poemcollection.routes.userRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    routing {

        val userDao by inject<IUserDao>()
        userRouting(userDao)

        val categoryDao by inject<ICategoryDao>()
        categoryRouting(categoryDao)
    }
}
