package com.poemcollection.controllers.categories

import com.poemcollection.controllers.BaseControllerTest
import com.poemcollection.controllers.categories.CategoryInstrumentation.givenACategory
import com.poemcollection.controllers.categories.CategoryInstrumentation.givenAValidInsertCategory
import com.poemcollection.controllers.categories.CategoryInstrumentation.givenAValidUpdateCategory
import com.poemcollection.data.dto.requests.category.CategoryDto
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.modules.categories.CategoryController
import com.poemcollection.modules.categories.CategoryControllerImpl
import com.poemcollection.statuspages.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.koin.dsl.module

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryControllerTest : BaseControllerTest() {

    private val categoryDao: ICategoryDao = mockk()
    private val controller: CategoryController by lazy { CategoryControllerImpl() }

    init {
        startInjection(
            module {
                single { categoryDao }
            }
        )
    }

    @BeforeEach
    override fun before() {
        super.before()
        clearMocks(categoryDao)
    }

    @Test
    fun `when creating category with correct information and category not taken, we return valid categoryDto`() {
        val postCategory = givenAValidInsertCategory()
        val createdCategory = givenACategory()

        // Setup
        coEvery { categoryDao.getCategoryByName(any()) } returns null
        coEvery { categoryDao.insertCategory(any()) } returns createdCategory

        runBlocking {
            // call to subject under test
            val responseCategory = controller.postCategory(postCategory)

            // Assertion
            assertThat(responseCategory.id).isEqualTo(createdCategory.id)
            assertThat(responseCategory.name).isEqualTo(createdCategory.name)
        }
    }

    @Test
    fun `when creating category with category already taken, we throw an error`() {
        val postCategory = givenAValidInsertCategory()
        val createdCategory = givenACategory()

        // Setup
        coEvery { categoryDao.getCategoryByName(any()) } returns createdCategory

        assertThrows<ErrorDuplicateEntity> {
            runBlocking { controller.postCategory(postCategory) }
        }
    }

    @Test
    fun `when creating category and database returns error, we throw exception`() {
        val postCategory = givenAValidInsertCategory()

        coEvery { categoryDao.getCategoryByName(any()) } returns null
        coEvery { categoryDao.insertCategory(any()) } returns null

        assertThrows<ErrorFailedCreate> {
            runBlocking { controller.postCategory(postCategory) }
        }
    }

    @Test
    fun `when requesting all categories, we return valid list`() {
        val createdCategory = givenACategory()

        coEvery { categoryDao.getCategories() } returns listOf(createdCategory)

        runBlocking {
            val responseCategories = controller.getAllCategories()

            assertThat(responseCategories).hasSize(1)
            assertThat(responseCategories).allMatch { it is CategoryDto }
        }
    }

    @Test
    fun `when requesting specific category, we return valid categoryDto`() {
        val createdCategory = givenACategory()

        coEvery { categoryDao.getCategory(any()) } returns createdCategory

        runBlocking {
            val responseCategory = controller.getCategoryById(1)

            // Assertion
            assertThat(responseCategory.id).isEqualTo(createdCategory.id)
            assertThat(responseCategory.name).isEqualTo(createdCategory.name)
        }
    }

    @Test
    fun `when requesting specific category which does not exist, we throw exception`() {
        coEvery { categoryDao.getCategory(any()) } throws ErrorNotFound

        assertThrows<ErrorNotFound> {
            runBlocking { controller.getCategoryById(1) }
        }
    }

    @Test
    fun `when updating specific category, we return valid categoryDto`() {
        val updateCategory = givenAValidUpdateCategory()
        val createdCategory = givenACategory()

        coEvery { categoryDao.updateCategory(any(), any()) } returns createdCategory

        runBlocking {
            val responseCategory = controller.updateCategoryById(1, updateCategory)

            // Assertion
            assertThat(responseCategory.id).isEqualTo(createdCategory.id)
            assertThat(responseCategory.name).isEqualTo(createdCategory.name)
        }
    }

    @Test
    fun `when updating specific category which does not exist, we throw exception`() {
        val updateCategory = givenAValidUpdateCategory()

        coEvery { categoryDao.updateCategory(any(), any()) } throws ErrorFailedUpdate

        assertThrows<ErrorFailedUpdate> {
            runBlocking { controller.updateCategoryById(1, updateCategory) }
        }
    }

    @Test
    fun `when deleting specific category, we return valid categoryDto`() {

        coEvery { categoryDao.deleteCategory(any()) } returns true

        assertDoesNotThrow {
            runBlocking {
                controller.deleteCategoryById(1)
            }
        }
    }

    @Test
    fun `when deleting specific category which does not exist, we throw exception`() {

        coEvery { categoryDao.deleteCategory(any()) } returns false

        assertThrows<ErrorFailedDelete> {
            runBlocking { controller.deleteCategoryById(1) }
        }
    }
}
