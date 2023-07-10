package com.poemcollection.data.database.dao

import com.poemcollection.data.database.tables.CategoriesTable
import com.poemcollection.data.database.tables.toCategories
import com.poemcollection.data.database.tables.toCategory
import com.poemcollection.data.dto.requests.category.InsertOrUpdateCategory
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.models.category.Category
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class CategoryDaoImpl : ICategoryDao {

    override fun getCategory(id: Int): Category? =
        CategoriesTable.select { (CategoriesTable.id eq id) }.toCategory()

    override fun getCategoryByName(name: String): Category? =
        CategoriesTable.select { (CategoriesTable.name eq name) }.toCategory()

    override fun getCategories(): List<Category> =
        CategoriesTable.selectAll().toCategories()

    override fun insertCategory(category: InsertOrUpdateCategory): Category? {
        return CategoriesTable.insert {
            val time = LocalDateTime.now().toDatabaseString()

            it[name] = category.name
            it[createdAt] = time
            it[updatedAt] = time
        }.resultedValues?.toCategory()
    }

    override fun updateCategory(id: Int, category: InsertOrUpdateCategory): Category? {
        CategoriesTable.update({ CategoriesTable.id eq id }) {
            it[name] = category.name
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        return getCategory(id)
    }

    override fun deleteCategory(id: Int): Boolean =
        CategoriesTable.deleteWhere { CategoriesTable.id eq id } > 0

    override fun getListOfExistingCategoryIds(categoryIds: List<Int>): List<Int> =
        CategoriesTable.select { CategoriesTable.id inList categoryIds }.map { it[CategoriesTable.id].value }
}