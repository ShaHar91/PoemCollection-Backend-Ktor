package com.poemcollection.controllers.auth

import com.auth0.jwt.interfaces.Payload
import com.poemcollection.controllers.BaseControllerTest
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.user.User
import com.poemcollection.modules.auth.validateUser
import com.poemcollection.modules.auth.validateUserIsAdmin
import io.ktor.server.auth.jwt.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.dsl.module
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthValidatorTest : BaseControllerTest() {

    private val userDao: IUserDao = mockk()
    private val payload: Payload = mockk()

    init {
        startInjection(module {
            single { userDao }
        })
    }

    @BeforeEach
    override fun before() {
        super.before()
        clearMocks(userDao)
    }

    @Test
    fun `when validating user where payload does not contain UserId, return null -- unauthorized`() {
        coEvery { payload.claims[any()]?.asInt() } returns null

        val jwt = JWTCredential(payload)

        runBlocking {
            val response = jwt.validateUser(databaseProvider, userDao)
            assertNull(response)
        }
    }

    @Test
    fun `when validating user where payload does not contain correct audience, return null -- unauthorized`() {
        coEvery { payload.claims[any()]?.asInt() } returns 1
        coEvery { userDao.getUser(any()) } returns User()
        coEvery { payload.audience.contains(any()) } returns false

        val jwt = JWTCredential(payload)

        runBlocking {
            val response = jwt.validateUser(databaseProvider, userDao)
            assertNull(response)
        }
    }

    @Test
    fun `when validating user where every thing is correct, return user as principal`() {
        coEvery { payload.claims[any()]?.asInt() } returns 1
        coEvery { userDao.getUser(any()) } returns User()
        coEvery { payload.audience.contains(any()) } returns true

        val jwt = JWTCredential(payload)

        runBlocking {
            val response = jwt.validateUser(databaseProvider, userDao)
            assertThat(response).isEqualTo(User())
        }
    }


    @Test
    fun `when validating user as admin where payload does not contain UserId, return null -- unauthorized`() {
        coEvery { payload.claims[any()]?.asInt() } returns null

        val jwt = JWTCredential(payload)

        runBlocking {
            val response = jwt.validateUserIsAdmin(databaseProvider, userDao)
            assertNull(response)
        }
    }

    @Test
    fun `when validating user as admin where payload does not contain correct audience, return null -- unauthorized`() {
        coEvery { payload.claims[any()]?.asInt() } returns 1
        coEvery { userDao.getUser(any()) } returns User()
        coEvery { userDao.isUserRoleAdmin(any()) } returns true
        coEvery { payload.audience.contains(any()) } returns false

        val jwt = JWTCredential(payload)

        runBlocking {
            val response = jwt.validateUserIsAdmin(databaseProvider, userDao)
            assertNull(response)
        }
    }

    @Test
    fun `when validating user as admin where user is not admin, return null -- unauthorized`() {
        coEvery { payload.claims[any()]?.asInt() } returns 1
        coEvery { userDao.getUser(any()) } returns User()
        coEvery { userDao.isUserRoleAdmin(any()) } returns false
        coEvery { payload.audience.contains(any()) } returns true

        val jwt = JWTCredential(payload)

        runBlocking {
            val response = jwt.validateUserIsAdmin(databaseProvider, userDao)
            assertNull(response)
        }
    }

    @Test
    fun `when validating user as admin where every thing is correct, return user as principal`() {
        coEvery { payload.claims[any()]?.asInt() } returns 1
        coEvery { userDao.getUser(any()) } returns User()
        coEvery { userDao.isUserRoleAdmin(any()) } returns true
        coEvery { payload.audience.contains(any()) } returns true

        val jwt = JWTCredential(payload)

        runBlocking {
            val response = jwt.validateUserIsAdmin(databaseProvider, userDao)
            assertThat(response).isEqualTo(User())
        }
    }
}