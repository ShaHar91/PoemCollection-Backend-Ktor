package com.poemcollection.routing.categories

import com.poemcollection.data.dto.requests.category.CategoryDto
import com.poemcollection.data.dto.requests.category.InsertOrUpdateCategory
import com.poemcollection.modules.categories.CategoryController
import com.poemcollection.modules.categories.categoryRouting
import com.poemcollection.routing.BaseRoutingTest
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
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
            install(Authentication) {
                jwtTest("admin")
            }
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
    fun `when creating category with successful insertion, we return response category body`() = withBaseTestApplication {
        val time = LocalDateTime.now().toDatabaseString()
        val categoryResponse = CategoryDto(1, "Love", time, time)
        coEvery { categoryController.postCategory(any()) } returns categoryResponse

        val body = toJsonBody(InsertOrUpdateCategory("Hate"))
        val call = handleRequest(HttpMethod.Post, "/categories") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Authorization, "Bearer $bearerToken")
            setBody(body)
        }

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(CategoryDto::class.java)
            assertThat(categoryResponse).isEqualTo(responseBody)
        }
    }
}