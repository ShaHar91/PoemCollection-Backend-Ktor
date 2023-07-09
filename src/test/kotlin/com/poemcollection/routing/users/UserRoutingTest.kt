package com.poemcollection.routing.users

import com.poemcollection.data.database.tables.UserRoles
import com.poemcollection.data.dto.requests.user.InsertNewUser
import com.poemcollection.data.dto.requests.user.UpdatePassword
import com.poemcollection.data.dto.requests.user.UserDto
import com.poemcollection.domain.models.user.User
import com.poemcollection.domain.models.user.toDto
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
import org.junit.jupiter.api.*
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
    fun `when creating user without any body, returns error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.postUser(any()) } throws Exception()

        val exception = assertThrows<Exception> {
            doCall(HttpMethod.Post, "/users/register")
        }
        assertThat(exception.message).isEqualTo(null)
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

    @Test
    fun `when creating user with any error, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.postUser(any()) } throws Exception()

        val body = toJsonBody(InsertNewUser("", "", "", "", ""))
        val exception = assertThrows<Exception> {
            doCall(HttpMethod.Post, "/users/register", body)
        }
        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when fetching current user, we return user`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val userResponse = User(1, "Chris", "Bol", "chris.bol@example.com", time, time).toDto()

        val call = doCall(HttpMethod.Get, "/users/me")

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(UserDto::class.java)
            assertThat(userResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when updating user with successful insertion, we return response user body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val userDto = UserDto(1, "Chri", "Bol", "chri.bol@example.com", time, time, UserRoles.User)
        coEvery { userController.updateUserById(any(), any()) } returns userDto

        val body = toJsonBody(InsertNewUser("", "", "", "", ""))
        val call = doCall(HttpMethod.Put, "/users/me", body)

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(UserDto::class.java)
            assertThat(userDto).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when updating user with any error, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.updateUserById(any(), any()) } throws Exception()

        val body = toJsonBody(InsertNewUser("", "", "", "", ""))
        val exception = assertThrows<Exception> {
            doCall(HttpMethod.Put, "/users/me", body)
        }
        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when updating user password with successful insertion, we return response user body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val userDto = UserDto(1, "Chri", "Bol", "chri.bol@example.com", time, time, UserRoles.User)
        coEvery { userController.updateUserPasswordById(any(), any()) } returns userDto

        val body = toJsonBody(UpdatePassword("", "", ""))
        val call = doCall(HttpMethod.Put, "/users/me/password", body)

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(UserDto::class.java)
            assertThat(userDto).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when updating user password with any error, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.updateUserPasswordById(any(), any()) } throws Exception()

        val body = toJsonBody(InsertNewUser("", "", "", "", ""))
        val exception = assertThrows<Exception> {
            doCall(HttpMethod.Put, "/users/me/password", body)
        }
        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when deleting user successful, we return Ok response`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.deleteUserById(any()) } returns Unit

        val call = doCall(HttpMethod.Delete, "/users/me")

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
        }
    }

    @Test
    fun `when deleting user with any error, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.deleteUserById(any()) } throws Exception()

        val exception = assertThrows<Exception> {
            doCall(HttpMethod.Delete, "/users/me")
        }
        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when fetching a specific user that exists, we return that user`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val userResponse = UserDto(1, "", "", "", time, time)
        coEvery { userController.getUserById(any()) } returns userResponse

        val call = doCall(HttpMethod.Get, "/users/1")

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(UserDto::class.java)
            assertThat(userResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when fetching a specific user that does not exist, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.getUserById(any()) } throws Exception()

        val exception = assertThrows<Exception> {
            doCall(HttpMethod.Get, "/users/1")
        }

        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when fetching a specific user, user not admin, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {

        val call = doCall(HttpMethod.Get, "/users/1")

        assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `when updating user by id with successful insertion, we return response user body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val userDto = UserDto(1, "Chri", "Bol", "chri.bol@example.com", time, time, UserRoles.User)
        coEvery { userController.updateUserById(any(), any()) } returns userDto

        val body = toJsonBody(InsertNewUser("", "", "", "", ""))
        val call = doCall(HttpMethod.Put, "/users/1", body)

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(UserDto::class.java)
            assertThat(userDto).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when updating user by id with any error, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.updateUserById(any(), any()) } throws Exception()

        val body = toJsonBody(InsertNewUser("", "", "", "", ""))
        val exception = assertThrows<Exception> {
            doCall(HttpMethod.Put, "/users/1", body)
        }
        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when updating user by id, user not admin, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {

        val call = doCall(HttpMethod.Put, "/users/1")

        assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `when updating user password by id, with successful insertion, we return response user body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val userDto = UserDto(1, "Chri", "Bol", "chri.bol@example.com", time, time, UserRoles.User)
        coEvery { userController.updateUserPasswordById(any(), any()) } returns userDto

        val body = toJsonBody(UpdatePassword("", "", ""))
        val call = doCall(HttpMethod.Put, "/users/1/password", body)

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(UserDto::class.java)
            assertThat(userDto).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when updating user password by id, with any error, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.updateUserPasswordById(any(), any()) } throws Exception()

        val body = toJsonBody(InsertNewUser("", "", "", "", ""))
        val exception = assertThrows<Exception> {
            doCall(HttpMethod.Put, "/users/1/password", body)
        }
        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when updating user password by id, user not admin, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {

        val call = doCall(HttpMethod.Put, "/users/1/password")

        assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `when deleting user successful by id, we return Ok response`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.deleteUserById(any()) } returns Unit

        val call = doCall(HttpMethod.Delete, "/users/1")

        call.also {
            assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
        }
    }

    @Test
    fun `when deleting user by id with any error, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly, UserRoles.Admin),
        AuthenticationInstrumentation()
    ) {
        coEvery { userController.deleteUserById(any()) } throws Exception()

        val exception = assertThrows<Exception> {
            doCall(HttpMethod.Delete, "/users/1")
        }
        assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when deleting user by id, user not admin, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {

        val call = doCall(HttpMethod.Put, "/users/1/password")

        assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }
}