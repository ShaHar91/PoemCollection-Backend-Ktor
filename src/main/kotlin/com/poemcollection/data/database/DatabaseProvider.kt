package com.poemcollection.data.database

import com.poemcollection.data.database.tables.*
import com.poemcollection.data.dto.requests.user.InsertNewUser
import com.poemcollection.modules.users.UserController
import com.poemcollection.utils.PasswordManagerContract
import com.poemcollection.utils.toDatabaseString
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime

class DatabaseProvider : DatabaseProviderContract, KoinComponent {

    private val config by inject<ApplicationConfig>()
    private val passwordEncryption by inject<PasswordManagerContract>()

//    private val dispatcher: CoroutineContext
//
//    init {
//        dispatcher = newFixedThreadPoolContext(5, "database-pool")
//    }

    override fun init() {
//        val name = config.property("ktor.deployment.databaseName").getString()
//        Database.connect("jdbc:sqlite:./$name")
        Database.connect("jdbc:mysql://localhost:3306/databaseTesting", user = "ChriBol", password = "U4N9TRnabmGqV3h")

        transaction {
            SchemaUtils.createMissingTablesAndColumns(UsersTable, CategoriesTable, PoemsTable, ReviewsTable, PoemCategoryJunctionTable)

            seedDatabase()
        }
    }

    private fun seedDatabase() {
        UsersTable.insertIgnore {
            val time = LocalDateTime.now().toDatabaseString()

            it[id] = 1
            it[firstName] = "Christiano"
            it[lastName] = "Bolla"
            it[email] = "bollachristiano@gmail.com"
            it[password] = passwordEncryption.encryptPassword("Test1234+@")
            it[createdAt] = time
            it[updatedAt] = time
            it[role] = UserRoles.Admin
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