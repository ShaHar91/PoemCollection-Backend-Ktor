package com.poemcollection.controllers.auth

import com.poemcollection.controllers.BaseControllerTest
import com.poemcollection.controllers.auth.AuthInstrumentation.givenAValidCreateToken
import com.poemcollection.controllers.auth.AuthInstrumentation.givenAnInvalidCreateToken
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.user.User
import com.poemcollection.model.CredentialsResponse
import com.poemcollection.modules.auth.AuthController
import com.poemcollection.modules.auth.AuthControllerImpl
import com.poemcollection.modules.auth.TokenProvider
import com.poemcollection.utils.PasswordManagerContract
import com.poemcollection.utils.TBDException
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.koin.dsl.module

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest : BaseControllerTest() {

    private val userDao: IUserDao = mockk()
    private val tokenProvider: TokenProvider = mockk()
    private val passwordEncryption: PasswordManagerContract = mockk()
    private val controller: AuthController by lazy { AuthControllerImpl() }

    init {
        startInjection(
            module {
                single { userDao }
                single { tokenProvider }
                single { passwordEncryption }
            }
        )
    }

    @BeforeEach
    override fun before() {
        super.before()
        clearMocks(userDao, tokenProvider, passwordEncryption)
    }

    @Test
    fun `when authorizing user with invalid email, we throw exception`() {
        assertThrows<TBDException> {
            runBlocking { controller.authorizeUser(givenAnInvalidCreateToken()) }
        }
    }

    @Test
    fun `when authorizing user which does not exist, we throw exception`() {
        coEvery { userDao.getUserHashableByEmail(any()) } returns null

        assertThrows<TBDException> {
            runBlocking { controller.authorizeUser(givenAValidCreateToken()) }
        }
    }

    @Test
    fun `when authorizing user and password is not correct, we throw exception`() {
        coEvery { userDao.getUserHashableByEmail(any()) } returns User()
        coEvery { passwordEncryption.validatePassword(any(), any()) } returns false

        assertThrows<TBDException> {
            runBlocking { controller.authorizeUser(givenAValidCreateToken()) }
        }
    }

    @Test
    fun `when authorizing user when everything is valid, we return an accessToken`() {
        val createdToken = CredentialsResponse("", "", 0)

        coEvery { userDao.getUserHashableByEmail(any()) } returns User()
        coEvery { passwordEncryption.validatePassword(any(), any()) } returns true
        coEvery { tokenProvider.createTokens(any()) } returns createdToken

        runBlocking {
            val response = controller.authorizeUser(givenAValidCreateToken())
            assertThat(response).isEqualTo(createdToken)
        }
    }
}
