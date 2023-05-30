package com.poemcollection.plugins

import com.poemcollection.data.local.dao.CategoryDaoImpl
import com.poemcollection.data.local.dao.PoemDaoImpl
import com.poemcollection.data.local.dao.ReviewDaoImpl
import com.poemcollection.data.local.dao.UserDaoImpl
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.routes.*
import com.poemcollection.routes.interfaces.*
import com.poemcollection.security.security.hashing.HashingService
import com.poemcollection.security.security.hashing.SHA256HashingService
import com.poemcollection.security.security.token.JwtTokenService
import com.poemcollection.security.security.token.TokenConfig
import com.poemcollection.security.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import java.util.concurrent.TimeUnit

fun Application.configureKoin(environment: ApplicationEnvironment) {
    install(Koin) {
        slf4jLogger()

        modules(module {
            single { environment.config }
        })
        modules(securityModule())
        modules(daoModule())
        modules(routeModule())
    }
}

fun securityModule() = module {
    single {
        val config = get<ApplicationConfig>()
        TokenConfig(
            config.property("jwt.issuer").getString(),
            config.property("jwt.audience").getString(),
            TimeUnit.HOURS.toMillis(1), // Valid for 1 hour...
            System.getenv("JWT_SECRET")
        )
    }

    // Both options are valid an represent the same
    singleOf(::SHA256HashingService) bind HashingService::class
    singleOf(::JwtTokenService) {
        bind<TokenService>()

//        --- Other options ---
//        named("a_qualifier") - give a String qualifier to the definition
//        named<MyType>() - give a Type qualifier to the definition
//        bind<MyInterface>() - add type to bind for given bean definition
//        binds(arrayOf(...)) - add types array for given bean definition
//        createdAtStart() - create single instance at Koin start
    }
}

fun routeModule() = module {
    singleOf(::UserRoutesImpl) { bind<IUserRoutes>() }
    singleOf(::AuthRoutesImpl) { bind<IAuthRoutes>() }
    singleOf(::CategoryRoutesImpl) { bind<ICategoryRoutes>() }
    singleOf(::PoemRoutesImpl) { bind<IPoemRoutes>() }
    singleOf(::ReviewRoutesImpl) { bind<IReviewRoutes>() }
}

fun daoModule() = module {
    singleOf(::UserDaoImpl) { bind<IUserDao>() }
    singleOf(::CategoryDaoImpl) { bind<ICategoryDao>() }
    singleOf(::PoemDaoImpl) { bind<IPoemDao>() }
    singleOf(::ReviewDaoImpl) { bind<IReviewDao>() }
}