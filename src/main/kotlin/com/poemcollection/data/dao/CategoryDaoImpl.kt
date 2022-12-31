package com.poemcollection.data.dao

import com.poemcollection.data.Categories
import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.data.models.Category
import com.poemcollection.data.models.InsertOrUpdateCategory
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class CategoryDaoImpl : ICategoryDao {

    override suspend fun getCategory(id: Int): Category? = dbQuery {
        Categories.select { Categories.id eq id }.toCategory().firstOrNull()
    }

    override suspend fun getCategories(): List<Category> = dbQuery {
        Categories.selectAll().toCategory()
    }

    override suspend fun insertCategory(category: InsertOrUpdateCategory): Category? = dbQuery {
        Categories.insert {
            it[name] = category.name
            it[createdAt] = LocalDateTime.now().toDatabaseString()
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }.resultedValues?.toCategories()?.singleOrNull()
    }

    override suspend fun updateCategory(id: Int, category: InsertOrUpdateCategory): Category? = dbQuery {
        val result = Categories.update({ Categories.id eq id }) {
            it[name] = category.name
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        if (result == 1) {
            Categories.select { Categories.id eq id }.toCategory().firstOrNull()
        } else {
            null
        }
    }

    override suspend fun deleteCategory(id: Int): Boolean = dbQuery {
        val result = Categories.deleteWhere { Categories.id eq id }

        result == 1
    }
}