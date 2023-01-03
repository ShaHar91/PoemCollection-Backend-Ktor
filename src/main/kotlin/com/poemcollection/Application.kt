package com.poemcollection

import com.poemcollection.data.DatabaseFactory
import com.poemcollection.plugins.*
import com.poemcollection.security.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(CORS) {
        anyHost()
    }

    DatabaseFactory.init()
    val tokenConfig = TokenConfig(
        environment.config.property("jwt.issuer").getString(),
        environment.config.property("jwt.audience").getString(),
        1000L * 60L * 60L, // Valid for 1 hour...
        System.getenv("JWT_SECRET")
    )

    configureSecurity(tokenConfig)
    configureValidation()
    configureKoin()
    configureRouting(tokenConfig)
    configureSerialization()
}


// TODO: can maybe be used for a couple of translations??   --->    https://github.com/aymanizz/ktor-i18n
// TODO: what does this do??    ---->    https://github.com/myndocs/kotlin-oauth2-server