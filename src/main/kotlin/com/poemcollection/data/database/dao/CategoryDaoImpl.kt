package com.poemcollection.data.database.dao

import com.poemcollection.data.database.CategoriesTable
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.models.category.Category
import com.poemcollection.domain.models.category.InsertOrUpdateCategory
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class CategoryDaoImpl : ICategoryDao {

    override suspend fun getCategory(id: Int): Category? =
        CategoriesTable.select { CategoriesTable.id eq id }.toCategory()

    override suspend fun getCategories(): List<Category> =
        CategoriesTable.selectAll().toCategories()

    override suspend fun insertCategory(category: InsertOrUpdateCategory): Category? {
        return CategoriesTable.insert {
            it[name] = category.name
            it[createdAt] = LocalDateTime.now().toDatabaseString()
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }.resultedValues?.toCategories()?.singleOrNull()
    }

    override suspend fun updateCategory(id: Int, category: InsertOrUpdateCategory): Category? {
        CategoriesTable.update({ CategoriesTable.id eq id }) {
            it[name] = category.name
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        return CategoriesTable.select { CategoriesTable.id eq id }.toCategory()
    }

    override suspend fun deleteCategory(id: Int): Boolean =
        CategoriesTable.deleteWhere { CategoriesTable.id eq id } > 0

    override suspend fun getListOfExistingCategoryIds(categoryIds: List<Int>): List<Int> =
        CategoriesTable.select { CategoriesTable.id inList categoryIds }.map { it[CategoriesTable.id].value }
}