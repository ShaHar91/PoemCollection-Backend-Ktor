package com.poemcollection.plugins

import com.poemcollection.data.dao.CategoryDaoImpl
import com.poemcollection.data.dao.PoemDaoImpl
import com.poemcollection.data.dao.ReviewDaoImpl
import com.poemcollection.data.dao.UserDaoImpl
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
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin(environment: ApplicationEnvironment) {
    install(Koin) {
        slf4jLogger()

        modules(securityModule(environment))
        modules(daoModule())
        modules(routeModule())
    }
}

fun securityModule(environment: ApplicationEnvironment) = module {
    single {
        TokenConfig(
            environment.config.property("jwt.issuer").getString(),
            environment.config.property("jwt.audience").getString(),
            1000L * 60L * 60L, // Valid for 1 hour...
            System.getenv("JWT_SECRET")
        )
    }
    single<HashingService> { SHA256HashingService() }
    single<TokenService> { JwtTokenService() }
}

fun routeModule() = module {
    single<IUserRoutes> { UserRoutesImpl(get(), get()) }
    single<IAuthRoutes> { AuthRoutesImpl(get(), get(), get(), get()) }
    single<ICategoryRoutes> { CategoryRoutesImpl(get()) }
    single<IPoemRoutes> { PoemRoutesImpl(get(), get(), get(), get()) }
    single<IReviewRoutes> { ReviewRoutesImpl(get(), get()) }
}

fun daoModule() = module {
    single<IUserDao> { UserDaoImpl() }
    single<ICategoryDao> { CategoryDaoImpl() }
    single<IPoemDao> { PoemDaoImpl() }
    single<IReviewDao> { ReviewDaoImpl() }
}