package com.poemcollection.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.Gson
import com.poemcollection.data.database.tables.UserRoles
import com.poemcollection.domain.models.user.User
import com.poemcollection.utils.toDatabaseString
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.ktor.plugin.Koin
import java.time.LocalDateTime

abstract class BaseRoutingTest {

    private val gson = Gson()
    protected var koinModules: Module? = null
    protected var moduleList: Application.() -> Unit = { }

    init {
        stopKoin()
    }

    fun <R> withBaseTestApplication(test: TestApplicationEngine.() -> R) {
        withTestApplication({
            install(ContentNegotiation) {
                gson()
            }
            koinModules?.let {
                install(Koin) {
                    modules(it)
                }
            }
            moduleList()
        }) {
            test()
        }
    }

    fun toJsonBody(obj: Any): String = gson.toJson(obj)

    fun <R> TestApplicationResponse.parseBody(clazz: Class<R>): R {
        return gson.fromJson(content, clazz)
    }

    fun AuthenticationConfig.jwtTest(name: String? = null) = jwt(name) {
        validate { User() }

        verifier(JWT.require(Algorithm.HMAC256("secret")).build())

        validate { credential ->
            val time = LocalDateTime.now().toDatabaseString()
            User(1, "Chris", "Bol", "chris.bol@example.com", time, time, UserRoles.Admin)
        }
    }

    val bearerToken = JWT.create().sign(Algorithm.HMAC256("secret"))
}