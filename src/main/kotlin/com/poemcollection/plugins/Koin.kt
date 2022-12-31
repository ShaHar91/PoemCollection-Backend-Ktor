package com.poemcollection.plugins

import com.poemcollection.data.dao.CategoryDaoImpl
import com.poemcollection.data.dao.PoemDaoImpl
import com.poemcollection.data.dao.ReviewDaoImpl
import com.poemcollection.data.dao.UserDaoImpl
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
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
    single<IUserDao> { UserDaoImpl() }
    single<ICategoryDao> { CategoryDaoImpl() }
    single<IPoemDao> { PoemDaoImpl() }
    single<IReviewDao> { ReviewDaoImpl() }
}