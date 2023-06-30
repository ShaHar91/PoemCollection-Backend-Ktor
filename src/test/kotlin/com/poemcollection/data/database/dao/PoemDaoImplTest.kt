package com.poemcollection.data.database.dao

import com.poemcollection.data.database.instrumentation.PoemInstrumentation.givenASecondValidInsertCategoryBody
import com.poemcollection.data.database.instrumentation.PoemInstrumentation.givenAValidInsertCategoryBody
import com.poemcollection.data.database.instrumentation.PoemInstrumentation.givenAValidInsertPoemBody
import com.poemcollection.data.database.instrumentation.PoemInstrumentation.givenAValidInsertWriterBody
import com.poemcollection.data.database.instrumentation.PoemInstrumentation.givenAValidUpdatePoemBody
import com.poemcollection.data.database.tables.*
import kotlinx.coroutines.delay
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PoemDaoImplTest : BaseDaoTest() {

    private val poemDao = PoemDaoImpl()
    private val userDao = UserDaoImpl()
    private val categoryDao = CategoryDaoImpl()

    @Test
    fun `insert correct data and then get data by id, return correct poem`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable) {
            // Setup
            val validCategory = givenAValidInsertCategoryBody()
            val category = categoryDao.insertCategory(validCategory)

            val validUser = givenAValidInsertWriterBody()
            val user = userDao.insertUser(validUser)

            val validPoem = givenAValidInsertPoemBody()
            val poemId = poemDao.insertPoem(validPoem, user?.id!!)?.id

            // When
            val poem = poemDao.getPoem(poemId!!)

            // Then
            assertThat(poem).matches {
                it?.body == validPoem.body &&
                        it.title == validPoem.title &&
                        it.writer?.id == user.id &&
                        it.categories.size == 1 &&
                        it.categories.contains(category) &&
                        it.createdAt == it.updatedAt
            }
        }
    }

    @Test
    fun `insert data where user is not existent, return error`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable) {
            // Setup
            val validCategory = givenAValidInsertCategoryBody()
            val category = categoryDao.insertCategory(validCategory)

            val validPoem = givenAValidInsertPoemBody()

            // When
            val poem = poemDao.insertPoem(validPoem, 1)

            // Then
            assertThat(poem).matches {
                it?.body == validPoem.body &&
                        it.title == validPoem.title &&
                        it.writer == null &&
                        it.categories.size == 1 &&
                        it.categories.contains(category) &&
                        it.createdAt == it.updatedAt
            }
        }
    }

    // Will probably never occur since categories can't be removed
    @Test
    fun `insert data where category is not existent, return null`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable) {
            // Setup
            val validUser = givenAValidInsertWriterBody()
            val user = userDao.insertUser(validUser)

            val validPoem = givenAValidInsertPoemBody()

            // When
            val poem = poemDao.insertPoem(validPoem, user?.id!!)

            // Then
            assertNull(poem)
        }
    }

    @Test
    fun `getPoem where item does not exists, returns 'null'`() {
        withTables(PoemsTable, PoemCategoryJunctionTable) {
            val poem = poemDao.getPoem(2)

            assertThat(poem).isNull()
        }
    }

    @Test
    fun `insertPoem where information is correct, database is storing info and returning the correct content`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable) {
            // Setup
            val validCategory = givenAValidInsertCategoryBody()
            val category = categoryDao.insertCategory(validCategory)

            val validUser = givenAValidInsertWriterBody()
            val user = userDao.insertUser(validUser)

            val validPoem = givenAValidInsertPoemBody()

            // When
            val poem = poemDao.insertPoem(validPoem, user?.id!!)

            // Then
            assertThat(poem).matches {
                it?.body == validPoem.body &&
                        it.title == validPoem.title &&
                        it.writer?.id == user.id &&
                        it.categories.size == 1 &&
                        it.categories.contains(category) &&
                        it.createdAt == it.updatedAt
            }
        }
    }

    @Test
    fun `getPoems but none exist return empty list`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable) {
            // When
            val list = poemDao.getPoems()

            // Then
            assertThat(list).isEmpty()
        }
    }

    @Test
    fun `getPoems return the list`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable) {
            // Setup
            val validCategory = givenAValidInsertCategoryBody()
            categoryDao.insertCategory(validCategory)

            val validUser = givenAValidInsertWriterBody()
            val user = userDao.insertUser(validUser)

            val validPoem = givenAValidInsertPoemBody()
            poemDao.insertPoem(validPoem, user?.id!!)

            val list = poemDao.getPoems()

            // Then
            assertThat(list).hasSize(1)
        }
    }

    @Test
    fun `getPoems return the list for the given category`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable) {
            // Setup
            val validCategory = givenAValidInsertCategoryBody()
            categoryDao.insertCategory(validCategory)

            val validUser = givenAValidInsertWriterBody()
            val user = userDao.insertUser(validUser)

            val validPoem = givenAValidInsertPoemBody()
            poemDao.insertPoem(validPoem, user?.id!!)

            // When
            val list = poemDao.getPoems(1)

            // Then
            assertThat(list).hasSize(1)
        }
    }

    @Test
    fun `isUserWriter return correct data`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable) {
            // Setup
            val validCategory = givenAValidInsertCategoryBody()
            categoryDao.insertCategory(validCategory)

            val validUser = givenAValidInsertWriterBody()
            val user = userDao.insertUser(validUser)

            val validPoem = givenAValidInsertPoemBody()
            val poemId = poemDao.insertPoem(validPoem, user?.id!!)?.id

            // When
            val isUserWriter = poemDao.isUserWriter(poemId!!, user.id)
            val isUserWriter2 = poemDao.isUserWriter(poemId, 392)

            // Then
            assertTrue(isUserWriter)
            assertFalse(isUserWriter2)
        }
    }

    @Test
    fun `updatePoem where information is correct, database is storing information and returning the correct content`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable) {
            // Setup
            val category = categoryDao.insertCategory(givenAValidInsertCategoryBody())
            val category2 = categoryDao.insertCategory(givenASecondValidInsertCategoryBody())

            val validUser = givenAValidInsertWriterBody()
            val user = userDao.insertUser(validUser)

            val poemId = poemDao.insertPoem(givenAValidInsertPoemBody(), user?.id!!)?.id

            val validUpdatePoem = givenAValidUpdatePoemBody()

            // adding a delay so there is a clear difference between `updatedAt` and `createdAt`
            delay(1000)

            // When
            val poem = poemDao.updatePoem(poemId!!, validUpdatePoem)

            // Then
            assertThat(poem).matches {
                it?.body == validUpdatePoem.body &&
                        it.title == validUpdatePoem.title &&
                        it.writer?.id == user.id &&
                        it.categories.size == 2 &&
                        it.categories.contains(category) &&
                        it.categories.contains(category2) &&
                        it.createdAt != it.updatedAt
            }
        }
    }

    @Test
    fun `deletePoem for id that does exist, return true`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable, ReviewsTable) {
            // Setup
            categoryDao.insertCategory(givenAValidInsertCategoryBody())

            val validUser = givenAValidInsertWriterBody()
            val user = userDao.insertUser(validUser)

            val poem = poemDao.insertPoem(givenAValidInsertPoemBody(), user?.id!!)

            // When
            val deleted = poemDao.deletePoem(poem?.id!!)

            // Then
            assertTrue(deleted)
        }
    }

    @Test
    fun `deletePoem for id that does not exists, return false`() {
        withTables(PoemsTable, UsersTable, CategoriesTable, PoemCategoryJunctionTable, ReviewsTable) {
            // Setup
            val deleted = poemDao.deletePoem(1)

            // Then
            assertFalse(deleted)
        }
    }
}