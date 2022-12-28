package com.poemcollection.plugins

import com.poemcollection.data.UserDao
import com.poemcollection.data.UserDaoImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(daoModule)
    }
}


val daoModule = module {
    single<UserDao> { UserDaoImpl() }
}