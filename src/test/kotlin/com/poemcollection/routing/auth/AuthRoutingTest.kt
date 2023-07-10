package com.poemcollection.routing.auth

import com.poemcollection.data.dto.requests.CreateTokenDto
import com.poemcollection.model.CredentialsResponse
import com.poemcollection.modules.auth.AuthController
import com.poemcollection.modules.auth.authRouting
import com.poemcollection.routing.BaseRoutingTest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.dsl.module

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthRoutingTest : BaseRoutingTest() {

    private val authController: AuthController = mockk()

    @BeforeAll
    fun setup() {
        koinModules = module {
            single { authController }
        }

        moduleList = {
            install(Routing) {
                authRouting()
            }
        }
    }

    @BeforeEach
    fun clearMocks() {
        clearMocks(authController)
    }

    @Test
    fun `when fetching all categories, we return a list`() = withBaseTestApplication {
        val authResponse = CredentialsResponse("", "", 0)
        coEvery { authController.authorizeUser(any()) } returns authResponse

        val body = toJsonBody(CreateTokenDto("", ""))
        val call = doCall(HttpMethod.Post, "/oauth/token", body)

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(CredentialsResponse::class.java)
            Assertions.assertThat(authResponse).isEqualTo(responseBody)
        }
    }
}