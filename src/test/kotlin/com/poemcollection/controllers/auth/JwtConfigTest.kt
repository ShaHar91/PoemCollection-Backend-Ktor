package com.poemcollection.controllers.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.poemcollection.domain.models.user.User
import com.poemcollection.modules.auth.JwtConfig
import io.ktor.server.config.*
import io.mockk.coEvery
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.util.*
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtConfigTest {

    @Test
    fun `when verifying a token with wrong secret key, we throw exception`() {
        val config = JwtConfig("https://127.0.0.1:8081", "users", "some-secret-key")

        assertThrows<SignatureVerificationException> {
            config.verifyToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.i1DDVAVnfKjFv8T2JZbASZ2KcIoJYTQrqmDyv-LLiro")
        }
    }

    @Test
    fun `when verifying correct tokens, we return the associated user id`() {
        val config = JwtConfig("https://127.0.0.1:8081", "users", "some-secret-key")

        val tokens = config.createTokens(User(1))

        val userId = config.verifyToken(tokens.access_token)
        val userIdRefresh = config.verifyToken(tokens.refresh_token)

        assertThat(userId).isEqualTo(1)
        assertThat(userIdRefresh).isEqualTo(1)
    }

    @Test
    fun `when creating tokens, we return tokens with the correct expiration date`() {
        val config = JwtConfig("https://127.0.0.1:8081", "users", "some-secret-key")

        val tokens = config.createTokens(User(1))

        val decodeToken = JWT.decode(tokens.access_token)
        val decodeRefreshToken = JWT.decode(tokens.refresh_token)

        assertThat(decodeToken.expiresAt).isCloseTo(Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)), 5000)
        assertThat(decodeRefreshToken.expiresAt).isCloseTo(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)), 5000)
    }

    @Test
    fun hello() {
        val appConfig = mockk<ApplicationConfig>()

        coEvery { appConfig.property("jwt.issuer").getString() } returns "http://1.1.1.1:8080"
        coEvery { appConfig.property("jwt.audience").getString() } returns "users"

        val config = JwtConfig(appConfig, "some-secret-key")

        val tokens = config.createTokens(User(1))

        val userId = config.verifyToken(tokens.access_token)
        val userIdRefresh = config.verifyToken(tokens.refresh_token)

        assertThat(userId).isEqualTo(1)
        assertThat(userIdRefresh).isEqualTo(1)
    }
}