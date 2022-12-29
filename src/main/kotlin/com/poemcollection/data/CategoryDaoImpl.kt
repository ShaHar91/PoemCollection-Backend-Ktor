package com.poemcollection.data

import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.models.Categories
import com.poemcollection.models.Category
import com.poemcollection.models.InsertOrUpdateCategory
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class CategoryDaoImpl : CategoryDao {

    private fun resultRowToCategory(row: ResultRow) = Category(
        id = row[Categories.id].value,
        name = row[Categories.name],
        createdAt = row[Categories.createdAt],
        updatedAt = row[Categories.updatedAt]
    )

    override suspend fun getCategory(id: Int): Category? = dbQuery {
        Categories.select { Categories.id eq id }.map(::resultRowToCategory).firstOrNull()
    }

    override suspend fun getCategories(): List<Category> = dbQuery {
        Categories.selectAll().map(::resultRowToCategory)
    }

    override suspend fun insertCategory(category: InsertOrUpdateCategory): Category? = dbQuery {
        Categories.insert {
            it[name] = category.name
            it[createdAt] = LocalDateTime.now().toDatabaseString()
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }.resultedValues?.map(::resultRowToCategory)?.singleOrNull()
    }

    override suspend fun updateCategory(id: Int, category: InsertOrUpdateCategory): Category? = dbQuery {
        val result = Categories.update({ Categories.id eq id }) {
            it[name] = category.name
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        if (result == 1) {
            Categories.select { Categories.id eq id }.map(::resultRowToCategory).firstOrNull()
        } else {
            null
        }
    }

    override suspend fun deleteCategory(id: Int): Boolean = dbQuery {
        val result = Categories.deleteWhere { Categories.id eq id }

        result == 1
    }
}