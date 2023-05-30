package com.poemcollection.modules

import com.poemcollection.data.database.DatabaseProviderContract
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseController : KoinComponent {

    private val dbProvider by inject<DatabaseProviderContract>()

    suspend fun <T> dbQuery(block: () -> T): T = dbProvider.dbQuery(block)
}