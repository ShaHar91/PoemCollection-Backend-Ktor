package com.poemcollection.data.database.dao

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach

abstract class BaseDaoTest {

    @BeforeEach
    open fun setup() {
        Database.connect(url = "jdbc:sqlite::memory:")
    }

    fun withTables(vararg tables: Table, test: suspend Transaction.() -> Unit) {
        transaction {
            SchemaUtils.create(*tables)
            try {
                runBlocking {
                    test()
                }
                commit()
            } finally {
                SchemaUtils.drop(*tables)
                commit()
            }
        }
    }
}