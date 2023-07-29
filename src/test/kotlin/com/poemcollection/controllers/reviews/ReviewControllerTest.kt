package com.poemcollection.controllers.reviews

import com.poemcollection.controllers.BaseControllerTest
import com.poemcollection.controllers.reviews.ReviewInstrumentation.givenAReview
import com.poemcollection.controllers.reviews.ReviewInstrumentation.givenAValidInsertReview
import com.poemcollection.controllers.reviews.ReviewInstrumentation.givenAValidUpdateReview
import com.poemcollection.data.dto.requests.review.ReviewDto
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.review.toDto
import com.poemcollection.modules.reviews.ReviewController
import com.poemcollection.modules.reviews.ReviewControllerImpl
import com.poemcollection.statuspages.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.koin.dsl.module

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewControllerTest : BaseControllerTest() {

    private val userDao: IUserDao = mockk()
    private val reviewDao: IReviewDao = mockk()
    private val controller: ReviewController by lazy { ReviewControllerImpl() }

    init {
        startInjection(
            module {
                single { userDao }
                single { reviewDao }
            }
        )
    }

    @BeforeEach
    override fun before() {
        super.before()
        clearMocks(userDao, reviewDao)
    }

    @Test
    fun `when creating new review, we return review object`() {
        val postReview = givenAValidInsertReview()
        val createdReview = givenAReview()

        coEvery { reviewDao.insertReview(any(), any(), any()) } returns createdReview

        runBlocking {
            val responseReview = controller.postReview(1, 1, postReview)

            assertThat(responseReview).isEqualTo(createdReview.toDto())
        }
    }

    @Test
    fun `when creating review and database return error, throw exception`() {
        val postReview = givenAValidInsertReview()

        coEvery { reviewDao.insertReview(any(), any(), any()) } returns null

        assertThrows<ErrorFailedCreate> {
            runBlocking { controller.postReview(1, 1, postReview) }
        }
    }

    @Test
    fun `when fetching all reviews for a poem, return list`() {
        val createdReview = givenAReview()

        coEvery { reviewDao.getReviews(any(), any()) } returns listOf(createdReview)

        runBlocking {
            val response = controller.getAllReviews(1, 1)

            assertThat(response).hasSize(1)
            assertThat(response).allMatch { it is ReviewDto }
        }
    }

    @Test
    fun `when fetching all reviews for a poem without limit, return list`() {
        val createdReview = givenAReview()

        coEvery { reviewDao.getReviews(any()) } returns listOf(createdReview)

        runBlocking {
            val response = controller.getAllReviews(1)

            assertThat(response).hasSize(1)
            assertThat(response).allMatch { it is ReviewDto }
        }
    }

    @Test
    fun `when fetching specific review, we return valid reviewDto`() {
        val createdReview = givenAReview()

        coEvery { reviewDao.getReview(any()) } returns createdReview

        runBlocking {
            val response = controller.getReviewById(1)

            assertThat(response).isEqualTo(createdReview.toDto())
        }
    }

    @Test
    fun `when fetching specific review which does not exist, we throw exception`() {
        coEvery { reviewDao.getReview(any()) } throws ErrorNotFound

        assertThrows<ErrorNotFound> {
            runBlocking { controller.getReviewById(1) }
        }
    }

    @Test
    fun `when updating specific review, we return valid reviewDto`() {
        val updateReview = givenAValidUpdateReview()
        val createdReview = givenAReview()

        coEvery { userDao.isUserRoleAdmin(any()) } returns true
        coEvery { reviewDao.isUserWriter(any(), any()) } returns true
        coEvery { reviewDao.updateReview(any(), any()) } returns createdReview

        runBlocking {
            val response = controller.updateReview(1, 1, updateReview)

            assertThat(response).isEqualTo(createdReview.toDto())
        }
    }

    @Test
    fun `when updating specific review and user is not admin and writer, we throw error`() {
        val updateReview = givenAValidUpdateReview()

        coEvery { userDao.isUserRoleAdmin(any()) } returns false
        coEvery { reviewDao.isUserWriter(any(), any()) } returns false

        assertThrows<ErrorUnauthorized> {
            runBlocking { controller.updateReview(1, 1, updateReview) }
        }
    }

    @Test
    fun `when updating specific review which does not exist, we throw error`() {
        val updateReview = givenAValidUpdateReview()

        coEvery { userDao.isUserRoleAdmin(any()) } returns true
        coEvery { reviewDao.isUserWriter(any(), any()) } returns true
        coEvery { reviewDao.updateReview(any(), any()) } throws ErrorFailedUpdate

        assertThrows<ErrorFailedUpdate> {
            runBlocking { controller.updateReview(1, 1, updateReview) }
        }
    }

    @Test
    fun `when deleting poem, we return without error`() {
        coEvery { userDao.isUserRoleAdmin(any()) } returns true
        coEvery { reviewDao.isUserWriter(any(), any()) } returns true
        coEvery { reviewDao.deleteReview(any()) } returns true

        assertDoesNotThrow {
            runBlocking { controller.deleteReview(1, 1) }
        }
    }

    @Test
    fun `when deleting poem and user is not admin and writer, we return with error`() {
        coEvery { userDao.isUserRoleAdmin(any()) } returns false
        coEvery { reviewDao.isUserWriter(any(), any()) } returns false

        assertThrows<ErrorUnauthorized> {
            runBlocking { controller.deleteReview(1, 1) }
        }
    }

    @Test
    fun `when deleting poem which does not exist, we throw exception`() {
        coEvery { userDao.isUserRoleAdmin(any()) } returns true
        coEvery { reviewDao.isUserWriter(any(), any()) } returns true
        coEvery { reviewDao.deleteReview(any()) } returns false

        assertThrows<ErrorFailedDelete> {
            runBlocking { controller.deleteReview(1, 1) }
        }
    }
}
