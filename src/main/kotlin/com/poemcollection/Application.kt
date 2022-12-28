package com.poemcollection

import com.poemcollection.data.DatabaseFactory
import io.ktor.server.application.*
import com.poemcollection.plugins.*
import io.ktor.server.plugins.cors.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(CORS) {
        anyHost()
    }

    DatabaseFactory.init()

    configureValidation()
    configureKoin()
    configureRouting()
    configureSerialization()
}




// TODO: can maybe be used for a couple of translations??   --->    https://github.com/aymanizz/ktor-i18n