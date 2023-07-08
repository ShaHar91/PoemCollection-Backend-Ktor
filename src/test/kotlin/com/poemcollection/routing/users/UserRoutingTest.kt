package com.poemcollection.routing.users

import com.poemcollection.data.database.tables.UserRoles
import com.poemcollection.data.dto.requests.user.InsertNewUser
import com.poemcollection.data.dto.requests.user.UserDto
import com.poemcollection.modules.auth.adminOnly
import com.poemcollection.modules.users.UserController
import com.poemcollection.modules.users.userRouting
import com.poemcollection.routing.AuthenticationInstrumentation
import com.poemcollection.routing.BaseRoutingTest
import com.poemcollection.utils.toDatabaseString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
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
class UserRoutingTest : BaseRoutingTest() {

    private val userController: UserController = mockk()

    @BeforeAll
    fun setup() {
        koinModules = module {
            single { userController }
        }

        moduleList = {
            install(Routing) {
                userRouting()
            }
        }
    }

    @BeforeEach
    fun clearMocks() {
        io.mockk.clearMocks(userController)
    }

    @Test
    fun `when creating user with successful insertion, we return response user body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val userDto = UserDto(1, "Chri", "Bol", "chri.bol@example.com", time, time, UserRoles.User)
        coEvery { userController.postUser(any()) } returns userDto

        val body = toJsonBody(InsertNewUser("", "", "", "", ""))
        val call = doCall(HttpMethod.Post, "/users/register", body)

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(UserDto::class.java)
            assertThat(userDto).isEqualTo(responseBody)
        }
    }
}