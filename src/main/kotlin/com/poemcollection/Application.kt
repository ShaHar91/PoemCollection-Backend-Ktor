package com.poemcollection

import com.auth0.jwt.interfaces.JWTVerifier
import com.poemcollection.data.database.DatabaseProvider
import com.poemcollection.data.database.DatabaseProviderContract
import com.poemcollection.data.database.dao.CategoryDaoImpl
import com.poemcollection.data.database.dao.PoemDaoImpl
import com.poemcollection.data.database.dao.ReviewDaoImpl
import com.poemcollection.data.database.dao.UserDaoImpl
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.modules.auth.AuthController
import com.poemcollection.modules.auth.AuthControllerImpl
import com.poemcollection.modules.auth.JwtConfig
import com.poemcollection.modules.auth.TokenProvider
import com.poemcollection.modules.categories.CategoryController
import com.poemcollection.modules.categories.CategoryControllerImpl
import com.poemcollection.modules.poems.PoemController
import com.poemcollection.modules.poems.PoemControllerImpl
import com.poemcollection.modules.reviews.ReviewController
import com.poemcollection.modules.reviews.ReviewControllerImpl
import com.poemcollection.modules.users.UserController
import com.poemcollection.modules.users.UserControllerImpl
import com.poemcollection.utils.PasswordManager
import com.poemcollection.utils.PasswordManagerContract
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.cors.routing.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.start() {
    install(CORS) {
        anyHost()
    }
    configureKoin()
    main()
}


// can maybe be used for a couple of translations??   --->    https://github.com/aymanizz/ktor-i18n
// what does this do??    ---->    https://github.com/myndocs/kotlin-oauth2-server

fun Application.configureKoin() {
    module {
        install(Koin) {
            modules(
                module {
                    single { environment.config }
                    singleOf(::DatabaseProvider) { bind<DatabaseProviderContract>() }
                    single<PasswordManagerContract> { PasswordManager }

                    single<TokenProvider> {
                        val config = get<ApplicationConfig>()
                        JwtConfig(config)
                    }
                    single<JWTVerifier> {
                        val tokenProvider = get<TokenProvider>()
                        tokenProvider.verifier
                    }
                },
                routeModule(),
                daoModule()
            )
        }
    }
}

fun Application.main() {
    module()
}

fun routeModule() = module {
    singleOf(::AuthControllerImpl) { bind<AuthController>() }
    singleOf(::UserControllerImpl) { bind<UserController>() }
    singleOf(::CategoryControllerImpl) { bind<CategoryController>() }
    singleOf(::PoemControllerImpl) { bind<PoemController>() }
    singleOf(::ReviewControllerImpl) { bind<ReviewController>() }
}

fun daoModule() = module {
    singleOf(::UserDaoImpl) { bind<IUserDao>() }
    singleOf(::CategoryDaoImpl) { bind<ICategoryDao>() }
    singleOf(::PoemDaoImpl) { bind<IPoemDao>() }
    singleOf(::ReviewDaoImpl) { bind<IReviewDao>() }
}