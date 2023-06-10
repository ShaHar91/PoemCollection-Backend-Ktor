package com.poemcollection.data.database.tables

import com.poemcollection.domain.models.category.Category
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime

object CategoriesTable : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex().default("")
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
}

fun ResultRow.toCategory() = Category(
    id = this[CategoriesTable.id].value,
    name = this[CategoriesTable.name],
    createdAt = this[CategoriesTable.createdAt],
    updatedAt = this[CategoriesTable.updatedAt]
)

fun Iterable<ResultRow>.toCategories() = this.map { it.toCategory() }
fun Iterable<ResultRow>.toCategory() = this.firstOrNull()?.toCategory()
