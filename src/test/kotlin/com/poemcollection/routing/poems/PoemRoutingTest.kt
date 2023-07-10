package com.poemcollection.routing.poems

import com.poemcollection.data.dto.requests.poem.InsertOrUpdatePoem
import com.poemcollection.data.dto.requests.poem.PoemDetailDto
import com.poemcollection.data.dto.requests.poem.PoemDto
import com.poemcollection.domain.models.Ratings
import com.poemcollection.modules.auth.adminOnly
import com.poemcollection.modules.poems.PoemController
import com.poemcollection.modules.poems.poemRouting
import com.poemcollection.routing.AuthenticationInstrumentation
import com.poemcollection.routing.BaseRoutingTest
import com.poemcollection.utils.TBDException
import com.poemcollection.utils.toDatabaseString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.koin.dsl.module
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PoemRoutingTest : BaseRoutingTest() {

    private val poemController: PoemController = mockk()

    @BeforeAll
    fun setup() {
        koinModules = module {
            single { poemController }
        }

        moduleList = {
            install(Routing) {
                poemRouting()
            }
        }
    }

    @BeforeEach
    fun clearMocks() {
        io.mockk.clearMocks(poemController)
    }

    @Test
    fun `when creating poem with successful insertion, we return response category body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val poemResponse = PoemDetailDto(1, "Title", "Body", null, emptyList(), time, time)
        coEvery { poemController.postPoem(any(), any()) } returns poemResponse

        val body = toJsonBody(InsertOrUpdatePoem())
        val call = doCall(HttpMethod.Post, "/poems", body)

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(PoemDetailDto::class.java)
            Assertions.assertThat(poemResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when creating poem, user not logged in, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val call = doCall(HttpMethod.Post, "/poems", authorized = false)

        Assertions.assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `when fetching all poems, we return a list`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { poemController.getAllPoems(null) } returns listOf(PoemDto())

        val call = doCall(HttpMethod.Get, "/poems")

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(List::class.java)
            Assertions.assertThat(responseBody).hasSize(1)
        }
    }

    @Test
    fun `when fetching all poems for category, we return a list`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { poemController.getAllPoems(any()) } returns emptyList()

        val call = doCall(HttpMethod.Get, "/poems?category_id=1")

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(List::class.java)
            Assertions.assertThat(responseBody).isEmpty()
        }
    }

    @Test
    fun `when fetching a specific poem that exists, we return that poem`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val poemResponse = PoemDetailDto(1, "", "", null, emptyList(), time, time)
        coEvery { poemController.getPoemById(any()) } returns poemResponse

        val call = doCall(HttpMethod.Get, "/poems/1")

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(PoemDetailDto::class.java)
            Assertions.assertThat(poemResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when fetching a specific poem that does not exists, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { poemController.getPoemById(any()) } throws TBDException

        val exception = assertThrows<TBDException> {
            doCall(HttpMethod.Get, "/poems/1")
        }

        Assertions.assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when updating poem with successful insertion, we return response poem body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val poemResponse = PoemDetailDto(1, "", "", null, emptyList(), time, time)
        coEvery { poemController.updatePoemById(any(), any(), any()) } returns poemResponse

        val body = toJsonBody(InsertOrUpdatePoem("title", "body", emptyList()))
        val call = doCall(HttpMethod.Put, "/poems/1", body)

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(PoemDetailDto::class.java)
            Assertions.assertThat(poemResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when updating poem with wrong poemId, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { poemController.updatePoemById(any(), any(), any()) } throws TBDException

        val body = toJsonBody(InsertOrUpdatePoem())
        val exception = assertThrows<TBDException> {
            doCall(HttpMethod.Put, "/poems/1", body)
        }
        Assertions.assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when deleting poem successful, we return Ok response`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { poemController.deletePoemById(any(), any()) } returns Unit

        val call = doCall(HttpMethod.Delete, "/poems/1")

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
        }
    }

    @Test
    fun `when deleting poem successful, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { poemController.deletePoemById(any(), any()) } throws TBDException

        val exception = assertThrows<TBDException> {
            doCall(HttpMethod.Delete, "/poems/1")
        }
        Assertions.assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when fetching ratings, we return data`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val ratingsResponse = Ratings()
        coEvery { poemController.getRatingsForPoem(any()) } returns ratingsResponse

        val call = doCall(HttpMethod.Get, "/poems/1/ratings")

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(Ratings::class.java)
            Assertions.assertThat(ratingsResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when fetching ratings with error, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { poemController.getRatingsForPoem(any()) } throws TBDException

        val exception = assertThrows<TBDException> {
            doCall(HttpMethod.Get, "/poems/1/ratings")
        }
        Assertions.assertThat(exception.message).isEqualTo(null)
    }
}