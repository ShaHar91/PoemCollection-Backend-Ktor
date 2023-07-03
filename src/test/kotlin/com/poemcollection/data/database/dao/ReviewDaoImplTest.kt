package com.poemcollection.data.database.dao

import com.poemcollection.data.database.instrumentation.ReviewInstrumentation.givenAValidInsertReviewBody
import com.poemcollection.data.database.instrumentation.ReviewInstrumentation.givenAValidUpdateReviewBody
import com.poemcollection.data.database.instrumentation.UserInstrumentation.givenAValidInsertUserBody
import com.poemcollection.data.database.tables.PoemsTable
import com.poemcollection.data.database.tables.ReviewsTable
import com.poemcollection.data.database.tables.UsersTable
import com.poemcollection.domain.models.Ratings
import kotlinx.coroutines.delay
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class ReviewDaoImplTest : BaseDaoTest() {

    private val reviewDao = ReviewDaoImpl()
    private val userDao = UserDaoImpl()

    @Test
    fun `getReview where item exists, return correct review`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val validReviewBody = givenAValidInsertReviewBody()
            val userId = userDao.insertUser(givenAValidInsertUserBody())?.id
            val reviewId = reviewDao.insertReview(1, userId!!, validReviewBody)?.id

            val review = reviewDao.getReview(reviewId!!)

            assertThat(review).matches {
                it?.body == validReviewBody.body &&
                        it.rating == validReviewBody.rating &&
                        it.user.id == userId &&
                        it.createdAt == it.updatedAt
            }
        }
    }

    @Test
    fun `getReview where item does not exist, return 'null'`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val review = reviewDao.getReview(1)

            assertNull(review)
        }
    }

    @Test
    fun `getReviews return the list for specific poemId`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val validReviewBody = givenAValidInsertReviewBody()
            val userId = userDao.insertUser(givenAValidInsertUserBody())?.id!!

            reviewDao.insertReview(1, userId, validReviewBody)
            reviewDao.insertReview(2, userId, validReviewBody)

            val list = reviewDao.getReviews(1)

            assertThat(list).hasSize(1)
        }
    }

    @Test
    fun `getReviews return the list for specific poemId with limit to 1`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val validReviewBody = givenAValidInsertReviewBody()
            val userId = userDao.insertUser(givenAValidInsertUserBody())?.id!!

            reviewDao.insertReview(1, userId, validReviewBody)
            reviewDao.insertReview(1, 2, validReviewBody)

            val list = reviewDao.getReviews(1, 1)

            assertThat(list).hasSize(1)
        }
    }

    @Test
    fun `insertReview twice with same poemId and userId, return constraint crash`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val validReviewBody = givenAValidInsertReviewBody()
            val userId = userDao.insertUser(givenAValidInsertUserBody())?.id!!

            reviewDao.insertReview(1, userId, validReviewBody)

            assertThrows<ExposedSQLException> {
                reviewDao.insertReview(1, userId, validReviewBody)
            }
        }
    }

    @Test
    fun `insertReview with correct information, database is storing review and returning the correct data`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val validReviewBody = givenAValidInsertReviewBody()
            val userId = userDao.insertUser(givenAValidInsertUserBody())?.id!!

            val review = reviewDao.insertReview(1, userId, validReviewBody)

            assertThat(review).matches {
                it?.body == validReviewBody.body &&
                        it.rating == validReviewBody.rating &&
                        it.user.id == userId &&
                        it.createdAt == it.updatedAt
            }
        }
    }

    @Test
    fun `updateReview with correct information, database is storing inforamtion and returning the correct content`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val userId = userDao.insertUser(givenAValidInsertUserBody())?.id!!

            val reviewId = reviewDao.insertReview(1, userId, givenAValidInsertReviewBody())?.id

            // adding a delay so there is a clear difference between `updatedAt` and `createdAt`
            delay(2000)

            val validUpdateReviewBody = givenAValidUpdateReviewBody()
            val review = reviewDao.updateReview(reviewId!!, validUpdateReviewBody)

            assertThat(review).matches {
                it?.body == validUpdateReviewBody.body &&
                        it.rating == validUpdateReviewBody.rating &&
                        it.user.id == userId &&
                        it.createdAt != it.updatedAt
            }
        }
    }

    @Test
    fun `deleteReview for id that exists, return true`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val validReviewBody = givenAValidInsertReviewBody()
            val userId = userDao.insertUser(givenAValidInsertUserBody())?.id!!

            reviewDao.insertReview(1, userId, validReviewBody)

            val deleted = reviewDao.deleteReview(1)

            assertTrue(deleted)
        }
    }

    @Test
    fun `deleteReview for id that does not exist, return false`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val deleted = reviewDao.deleteReview(1)

            assertFalse(deleted)
        }
    }

    @Test
    fun `calculateRatings where review id does not exist, return all 0`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val ratings = reviewDao.calculateRatings(1)

            assertThat(ratings).matches {
                it == Ratings()
            }
        }
    }

    @Test
    fun `calculateRatings where multiple reviews exists for review id, return calculated ratings`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val userId = userDao.insertUser(givenAValidInsertUserBody())?.id!!
            val userId2 = userDao.insertUser(givenAValidInsertUserBody().copy(email = "1"))?.id!!
            val userId3 = userDao.insertUser(givenAValidInsertUserBody().copy(email = "2"))?.id!!
            val userId4 = userDao.insertUser(givenAValidInsertUserBody().copy(email = "3"))?.id!!

            reviewDao.insertReview(1, userId, givenAValidInsertReviewBody())?.id
            reviewDao.insertReview(1, userId2, givenAValidInsertReviewBody().copy(rating = 1))?.id
            reviewDao.insertReview(1, userId3, givenAValidInsertReviewBody().copy(rating = 1))?.id
            reviewDao.insertReview(1, userId4, givenAValidInsertReviewBody())?.id

            val ratings = reviewDao.calculateRatings(1)

            assertThat(ratings).matches {
                it == Ratings(total = 4, two_star = 2, one_star = 2, average = 1.5)
            }
        }
    }

    @Test
    fun `isUserWriter return correct data`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val userId = userDao.insertUser(givenAValidInsertUserBody())?.id!!

            val review = reviewDao.insertReview(1, userId, givenAValidInsertReviewBody())

            val isUserWriter = reviewDao.isUserWriter(review?.id!!, userId)
            val isUserWriter2 = reviewDao.isUserWriter(review.id, 243)

            assertTrue(isUserWriter)
            assertFalse(isUserWriter2)
        }
    }
}