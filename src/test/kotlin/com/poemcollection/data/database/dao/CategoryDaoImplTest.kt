package com.poemcollection.data.database.dao

import com.poemcollection.data.database.instrumentation.CategoryInstrumentation.givenAValidInsertCategoryBody
import com.poemcollection.data.database.instrumentation.CategoryInstrumentation.givenAValidUpdateCategoryBody
import com.poemcollection.data.database.tables.CategoriesTable
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CategoryDaoImplTest : BaseDaoTest() {

    private val dao = CategoryDaoImpl()

    @Test
    fun `getCategory where item exists, return correct category`() {
        withTables(CategoriesTable) {
            val validCategory = givenAValidInsertCategoryBody()
            val categoryId = dao.insertCategory(validCategory)?.id
            val category = dao.getCategory(categoryId!!)

            assertThat(category).matches {
                it?.name == "Love" && it.id == 1
            }
        }
    }

    @Test
    fun `getCategory where item does not exists, returns 'null'`() {
        withTables(CategoriesTable) {
            val category = dao.getCategory(903)

            assertThat(category).isNull()
        }
    }

    @Test
    fun `insertCategory where information is correct, database is storing category and returning the correct content`() {
        withTables(CategoriesTable) {
            val validCategory = givenAValidInsertCategoryBody()
            val category = dao.insertCategory(validCategory)
            assertThat(category).matches {
                it?.name == "Love" && it.id == 1
            }
        }
    }

    @Test
    fun `insertCategory where the same data exists, database will give error`() {
        withTables(CategoriesTable) {
            val validCategory = givenAValidInsertCategoryBody()
            dao.insertCategory(validCategory)

            assertThrows<ExposedSQLException> {
                dao.insertCategory(validCategory)
            }
        }
    }

    @Test
    fun `updateCategory where information is correct, database is storing information and returning the correct content`() {
        withTables(CategoriesTable) {
            val validCategory = givenAValidInsertCategoryBody()
            val categoryId = dao.insertCategory(validCategory)?.id
            val validUpdateCategory = givenAValidUpdateCategoryBody()
            val category = dao.updateCategory(categoryId!!, validUpdateCategory)

            assertThat(category).matches {
                it?.name == "Family" && it.id == 1
            }
        }
    }

    @Test
    fun `updateCategory with correct information but category with id does not exist, database does nothing and returns 'null'`() {
        withTables(CategoriesTable) {
            val validUpdateCategory = givenAValidUpdateCategoryBody()
            val category = dao.updateCategory(203, validUpdateCategory)

            assertThat(category).isNull()
        }
    }

    @Test
    fun `getCategories but none exist return empty list`() {
        withTables(CategoriesTable) {
            val list = dao.getCategories()
            assertThat(list).isEmpty()
        }
    }

    @Test
    fun `getCategories return the list`() {
        withTables(CategoriesTable) {
            dao.insertCategory(givenAValidInsertCategoryBody())
            val list = dao.getCategories()
            assertThat(list).hasSize(1)
        }
    }

    @Test
    fun `deleteCategory for id that exists, return true`() {
        withTables(CategoriesTable) {
            val id = dao.insertCategory(givenAValidInsertCategoryBody())?.id
            val deleted = dao.deleteCategory(id!!)
            assertTrue(deleted)
        }
    }

    @Test
    fun `deleteCategory for id that does not exists, return false`() {
        withTables(CategoriesTable) {
            val deleted = dao.deleteCategory(839)
            assertFalse(deleted)
        }
    }
}