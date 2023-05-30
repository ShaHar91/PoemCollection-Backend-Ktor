package com.poemcollection

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
import com.poemcollection.modules.categories.CategoryController
import com.poemcollection.modules.categories.CategoryControllerImpl
import com.poemcollection.plugin.configureCallLogging
import com.poemcollection.plugin.configureSerialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.start() {
    install(CORS) {
        anyHost()
    }
    configureKoin()
    configureSerialization()
    configureCallLogging()

    val databaseProvider by inject<DatabaseProviderContract>()
    // Init database here
    databaseProvider.init()
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
                },
                routeModule(),
                daoModule()
            )
        }
    }
}

fun routeModule() = module {
    singleOf(::AuthControllerImpl) { bind<AuthController>() }
    singleOf(::CategoryControllerImpl) { bind<CategoryController>() }
}

fun daoModule() = module {
    singleOf(::UserDaoImpl) { bind<IUserDao>() }
    singleOf(::CategoryDaoImpl) { bind<ICategoryDao>() }
    singleOf(::PoemDaoImpl) { bind<IPoemDao>() }
    singleOf(::ReviewDaoImpl) { bind<IReviewDao>() }
}