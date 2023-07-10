package com.poemcollection.routing.reviews

import com.poemcollection.data.dto.requests.review.InsertOrUpdateReview
import com.poemcollection.data.dto.requests.review.ReviewDto
import com.poemcollection.data.dto.requests.user.UserDto
import com.poemcollection.modules.auth.adminOnly
import com.poemcollection.modules.reviews.ReviewController
import com.poemcollection.modules.reviews.reviewRouting
import com.poemcollection.routing.AuthenticationInstrumentation
import com.poemcollection.routing.BaseRoutingTest
import com.poemcollection.utils.TBDException
import com.poemcollection.utils.toDatabaseString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.koin.dsl.module
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewRoutingTest : BaseRoutingTest() {

    private val reviewController: ReviewController = mockk()

    @BeforeAll
    fun setup() {
        koinModules = module {
            single { reviewController }
        }

        moduleList = {
            install(Routing) {
                reviewRouting()
            }
        }
    }

    @BeforeEach
    fun clearMocks() {
        io.mockk.clearMocks(reviewController)
    }

    @Test
    fun `when creating review with successful insertion, we return response review body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val reviewResponse = ReviewDto(1, "Review body", 0, UserDto(), time, time)
        coEvery { reviewController.postReview(any(), any(), any()) } returns reviewResponse

        val body = toJsonBody(InsertOrUpdateReview("This is not a good poem!", 0))
        val call = doCall(HttpMethod.Post, "/poems/1/reviews", body)

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(ReviewDto::class.java)
            Assertions.assertThat(reviewResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when creating review, user not logged in, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val body = toJsonBody(InsertOrUpdateReview("This is not a good poem!", 0))
        val call = doCall(HttpMethod.Post, "/poems/1/reviews", body, false)

        Assertions.assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `when fetching all reviews for poem, we return a list`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { reviewController.getAllReviews(any(), any()) } returns emptyList()

        val call = doCall(HttpMethod.Get, "/poems/1/reviews")

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(List::class.java)
            Assertions.assertThat(responseBody).isEmpty()
        }
    }

    @Test
    fun `when fetching a specific review that exists, we return that review`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val reviewResponse = ReviewDto(1, "Review body", 0, UserDto(), time, time)
        coEvery { reviewController.getReviewById(any()) } returns reviewResponse

        val call = doCall(HttpMethod.Get, "/poems/1/reviews/1")

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(ReviewDto::class.java)
            Assertions.assertThat(reviewResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when fetching a specific review that does not exists, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { reviewController.getReviewById(any()) } throws TBDException

        val exception = assertThrows<TBDException> {
            doCall(HttpMethod.Get, "/poems/1/reviews/1")
        }

        Assertions.assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when updating review with successful insertion, we return response review body`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val time = LocalDateTime.now().toDatabaseString()
        val reviewResponse = ReviewDto(1, "Review body", 0, UserDto(), time, time)
        coEvery { reviewController.updateReview(any(), any(), any()) } returns reviewResponse

        val body = toJsonBody(InsertOrUpdateReview("This is not a good poem!", 0))
        val call = doCall(HttpMethod.Put, "/poems/1/reviews/1", body)

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
            val responseBody = it.response.parseBody(ReviewDto::class.java)
            Assertions.assertThat(reviewResponse).isEqualTo(responseBody)
        }
    }

    @Test
    fun `when updating review that has error, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { reviewController.updateReview(any(), any(), any()) } throws TBDException

        val body = toJsonBody(InsertOrUpdateReview("This is not a good poem!", 0))
        val exception = assertThrows<TBDException> {
            doCall(HttpMethod.Put, "/poems/1/reviews/1", body)
        }

        Assertions.assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when updating review, user not logged in, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val body = toJsonBody(InsertOrUpdateReview("This is not a good poem!", 0))
        val call = doCall(HttpMethod.Put, "/poems/1/reviews/1", body, false)

        Assertions.assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `when deleting review successful, we return Ok response`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { reviewController.deleteReview(any(), any()) } returns Unit

        val call = doCall(HttpMethod.Delete, "/poems/1/reviews/1")

        call.also {
            Assertions.assertThat(HttpStatusCode.OK).isEqualTo(it.response.status())
        }
    }

    @Test
    fun `when deleting review with wrong reviewId, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        coEvery { reviewController.deleteReview(any(), any()) } throws TBDException

        val exception = assertThrows<TBDException> {
            doCall(HttpMethod.Delete, "/poems/1/reviews/1")
        }

        Assertions.assertThat(exception.message).isEqualTo(null)
    }

    @Test
    fun `when deleting review, user not logged in, we return error`() = withBaseTestApplication(
        AuthenticationInstrumentation(adminOnly),
        AuthenticationInstrumentation()
    ) {
        val call = doCall(HttpMethod.Delete, "/poems/1/reviews/1", authorized = false)

        Assertions.assertThat(call.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
    }
}