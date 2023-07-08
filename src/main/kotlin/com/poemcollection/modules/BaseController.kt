package com.poemcollection.modules

import com.poemcollection.data.database.DatabaseProviderContract
import com.poemcollection.statuspages.ApiException
import com.poemcollection.statuspages.InternalServerException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseController : KoinComponent {

    private val dbProvider by inject<DatabaseProviderContract>()

    suspend fun <T> dbQuery(block: () -> T): T = dbProvider.dbQuery(block)

    fun <T> safeExposed(function: () -> T): T {
        return try {
            function()
        } catch (e: ExposedSQLException) {
            if (e.message?.contains("SQLITE_CONSTRAINT_UNIQUE") == true) {
                throw parseExceptionType(ExceptionType.UniqueConstraint)
            }

            throw InternalServerException(e.localizedMessage)
        }
    }

    open fun parseExceptionType(exceptionType: ExceptionType): ApiException = InternalServerException()
}

enum class ExceptionType {
    UniqueConstraint
}