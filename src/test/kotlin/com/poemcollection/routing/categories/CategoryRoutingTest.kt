package com.poemcollection.routing.categories

import com.poemcollection.data.database.tables.UserRoles
import com.poemcollection.data.dto.requests.category.CategoryDto
import com.poemcollection.data.dto.requests.category.InsertOrUpdateCategory
import com.poemcollection.modules.auth.adminOnly
import com.poemcollection.modules.categories.CategoryController
import com.poemcollection.modules.categories.categoryRouting
import com.poemcollection.routing.AuthenticationInstrumentation
import com.poemcollection.routing.BaseRoutingTest
import com.poemcollection.statuspages.InvalidCategoryException
import com.poemcollection.utils.TBDException
import com.poemcollection.utils.toDatabaseString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.koin.dsl.module
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryRoutingTest : BaseRoutingTest() {

    private val categoryController: CategoryController = mockk()

    @BeforeAll
    fun setup() {
        koinModules = module {
            single { categoryController }
        }

        moduleList = {
            install(Routing) {
                categoryRouting()
            }
        }
    }

    @BeforeEach
    fun clearMocks() {
        clearMocks(categoryController)
    }

    @Test
    fun `when fetching all categories, we return a list`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly)
    ) {
        coEvery { categoryController.getAllCategories() } returns emptyList()

        val call = doCall(HttpMethod.Get, "/categories")

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(List::class.java)
            assertThat(responseBody).isEmpty()
        }
    }

    @Test
    fun `when fetching a specific category that exists, we return that category`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly)
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val categoryResponse = CategoryDto(1, "Love", time, time)
        coEvery { categoryController.getCategoryById(any()) } returns categoryResponse

        val call = doCall(HttpMethod.Get, "/categories/1")

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(CategoryDto::class.java)
            assertThat(categoryResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when fetching a specific category that does not exists, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly)
    ) {
        coEvery { categoryController.getCategoryById(any()) } throws TBDException

        val exception = assertThrows<TBDException> {
            doCall(HttpMethod.Get, "/categories/1")
        }

        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when creating category with successful insertion, we return response category body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin)
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val categoryResponse = CategoryDto(1, "Love", time, time)
        coEvery { categoryController.postCategory(any()) } returns categoryResponse

        val body = toJsonBody(InsertOrUpdateCategory("Hate"))
        val call = doCall(HttpMethod.Post, "/categories", body)

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(CategoryDto::class.java)
            assertThat(categoryResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when creating category already created, we return 400 error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin)
    ) {
        coEvery { categoryController.postCategory(any()) } throws InvalidCategoryException

        val body = toJsonBody(InsertOrUpdateCategory("Hate"))
        val exception = assertThrows<InvalidCategoryException> {
            doCall(HttpMethod.Post, "/categories", body)
        }
        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when creating category, user not admin, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.User)
    ) {
        val body = toJsonBody(InsertOrUpdateCategory("Hate"))
        val call = doCall(HttpMethod.Post, "/categories", body)

        assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `when updating category with successful insertion, we return response category body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin)
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val categoryResponse = CategoryDto(1, "Love", time, time)
        coEvery { categoryController.updateCategoryById(any(), any()) } returns categoryResponse

        val body = toJsonBody(InsertOrUpdateCategory("Hate"))
        val call = doCall(HttpMethod.Put, "/categories/1", body)

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(CategoryDto::class.java)
            assertThat(categoryResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when updating category with wrong categoryId, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin)
    ) {
        coEvery { categoryController.updateCategoryById(any(), any()) } throws TBDException

        val body = toJsonBody(InsertOrUpdateCategory("Hate"))
        val exception = assertThrows<TBDException> {
            doCall(HttpMethod.Put, "/categories/1", body)
        }
        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when updating category, user not admin, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.User)
    ) {
        val body = toJsonBody(InsertOrUpdateCategory("Hate"))
        val call = doCall(HttpMethod.Put, "/categories/1", body)

        assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `when deleting category successful, we return Ok response`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin)
    ) {
        coEvery { categoryController.deleteCategoryById(any()) } returns Unit

        val call = doCall(HttpMethod.Delete, "/categories/1")

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
        }
    }

    @Test
    fun `when deleting category with wrong categoryId, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin)
    ) {
        coEvery { categoryController.deleteCategoryById(any()) } throws TBDException

        val exception = assertThrows<TBDException> {
            doCall(HttpMethod.Delete, "/categories/1")
        }
        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when deleting category, user not admin, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.User)
    ) {
        val call = doCall(HttpMethod.Delete, "/categories/1")

        assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }
}