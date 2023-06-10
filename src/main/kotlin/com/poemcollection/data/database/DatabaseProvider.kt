package com.poemcollection.data.database

import com.poemcollection.data.database.tables.CategoriesTable
import com.poemcollection.data.database.tables.PoemsTable
import com.poemcollection.data.database.tables.ReviewsTable
import com.poemcollection.data.database.tables.UsersTable
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DatabaseProvider : DatabaseProviderContract, KoinComponent {

    private val config by inject<ApplicationConfig>()
//    private val dispatcher: CoroutineContext
//
//    init {
//        dispatcher = newFixedThreadPoolContext(5, "database-pool")
//    }

    override fun init() {
        val name = config.property("ktor.deployment.databaseName").getString()
        Database.connect("jdbc:sqlite:./$name")
        transaction {
            SchemaUtils.create(UsersTable, CategoriesTable, PoemsTable, ReviewsTable)
        }
    }

    override suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }
}

interface DatabaseProviderContract {
    fun init()
    suspend fun <T> dbQuery(block: () -> T): T
}