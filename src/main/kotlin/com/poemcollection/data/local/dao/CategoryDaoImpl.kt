package com.poemcollection.data.local.dao

import com.poemcollection.data.CategoriesTable
import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.data.requests.InsertOrUpdateCategoryReq
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.models.Category
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class CategoryDaoImpl : ICategoryDao {

    override suspend fun getCategory(id: Int): Category? = dbQuery {
        CategoriesTable.select { CategoriesTable.id eq id }.toCategory()
    }

    override suspend fun getCategories(): List<Category> = dbQuery {
        CategoriesTable.selectAll().toCategories()
    }

    override suspend fun insertCategory(category: InsertOrUpdateCategoryReq): Category? = dbQuery {
        CategoriesTable.insert {
            it[name] = category.name
            it[createdAt] = LocalDateTime.now().toDatabaseString()
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }.resultedValues?.toCategories()?.singleOrNull()
    }

    override suspend fun updateCategory(id: Int, category: InsertOrUpdateCategoryReq): Category? = dbQuery {
        val result = CategoriesTable.update({ CategoriesTable.id eq id }) {
            it[name] = category.name
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        if (result == 1) {
            CategoriesTable.select { CategoriesTable.id eq id }.toCategory()
        } else {
            null
        }
    }

    override suspend fun deleteCategory(id: Int): Boolean = dbQuery {
        val result = CategoriesTable.deleteWhere { CategoriesTable.id eq id }

        result == 1
    }

    override suspend fun getListOfExistingCategoryIds(categoryIds: List<Int>): List<Int> = dbQuery {
        CategoriesTable.select { CategoriesTable.id inList categoryIds }.map { it[CategoriesTable.id].value }
    }
}