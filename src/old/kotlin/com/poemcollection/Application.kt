package com.poemcollection

import com.poemcollection.data.local.DatabaseFactory
import com.poemcollection.plugins.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(CORS) {
        anyHost()
    }

    DatabaseFactory.init()

    configureKoin(environment)
    configureStatusPages()
    configureSecurity()
    configureValidation()
    configureRouting()
    configureSerialization()
}


// can maybe be used for a couple of translations??   --->    https://github.com/aymanizz/ktor-i18n
// what does this do??    ---->    https://github.com/myndocs/kotlin-oauth2-server