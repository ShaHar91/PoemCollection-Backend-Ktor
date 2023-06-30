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
            userDao.insertUser(givenAValidInsertUserBody())
            val reviewId = reviewDao.insertReview(1, validReviewBody)?.id

            val review = reviewDao.getReview(reviewId!!)

            assertThat(review).matches {
                it?.body == validReviewBody.body &&
                        it.rating == validReviewBody.rating &&
                        it.user.id == validReviewBody.userId &&
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
            userDao.insertUser(givenAValidInsertUserBody())

            reviewDao.insertReview(1, validReviewBody)
            reviewDao.insertReview(2, validReviewBody)

            val list = reviewDao.getReviews(1)

            assertThat(list).hasSize(1)
        }
    }

    @Test
    fun `getReviews return the list for specific poemId with limit to 1`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val validReviewBody = givenAValidInsertReviewBody()
            userDao.insertUser(givenAValidInsertUserBody())

            reviewDao.insertReview(1, validReviewBody)
            reviewDao.insertReview(1, validReviewBody.copy(userId = 2))

            val list = reviewDao.getReviews(1, 1)

            assertThat(list).hasSize(1)
        }
    }

    @Test
    fun `insertReview twice with same poemId and userId, return constraint crash`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val validReviewBody = givenAValidInsertReviewBody()
            userDao.insertUser(givenAValidInsertUserBody())

            reviewDao.insertReview(1, validReviewBody)

            assertThrows<ExposedSQLException> {
                reviewDao.insertReview(1, validReviewBody)
            }
        }
    }

    @Test
    fun `insertReview with correct information, database is storing review and returning the correct data`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val validReviewBody = givenAValidInsertReviewBody()
            userDao.insertUser(givenAValidInsertUserBody())

            val review = reviewDao.insertReview(1, validReviewBody)

            assertThat(review).matches {
                it?.body == validReviewBody.body &&
                        it.rating == validReviewBody.rating &&
                        it.user.id == validReviewBody.userId &&
                        it.createdAt == it.updatedAt
            }
        }
    }

    @Test
    fun `updateReview with correct information, database is storing inforamtion and returning the correct content`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            userDao.insertUser(givenAValidInsertUserBody())

            val reviewId = reviewDao.insertReview(1, givenAValidInsertReviewBody())?.id

            // adding a delay so there is a clear difference between `updatedAt` and `createdAt`
            delay(2000)

            val validUpdateReviewBody = givenAValidUpdateReviewBody()
            val review = reviewDao.updateReview(reviewId!!, validUpdateReviewBody)

            assertThat(review).matches {
                it?.body == validUpdateReviewBody.body &&
                        it.rating == validUpdateReviewBody.rating &&
                        it.user.id == validUpdateReviewBody.userId &&
                        it.createdAt != it.updatedAt
            }
        }
    }

    @Test
    fun `deleteReview for id that exists, return true`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val validReviewBody = givenAValidInsertReviewBody()
            userDao.insertUser(givenAValidInsertUserBody())

            reviewDao.insertReview(1, validReviewBody)

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
            userDao.insertUser(givenAValidInsertUserBody())
            userDao.insertUser(givenAValidInsertUserBody().copy(email = "1"))
            userDao.insertUser(givenAValidInsertUserBody().copy(email = "2"))
            userDao.insertUser(givenAValidInsertUserBody().copy(email = "3"))

            reviewDao.insertReview(1, givenAValidInsertReviewBody())?.id
            reviewDao.insertReview(1, givenAValidInsertReviewBody().copy(userId = 2, rating = 1))?.id
            reviewDao.insertReview(1, givenAValidInsertReviewBody().copy(userId = 3, rating = 1))?.id
            reviewDao.insertReview(1, givenAValidInsertReviewBody().copy(userId = 4))?.id

            val ratings = reviewDao.calculateRatings(1)

            assertThat(ratings).matches {
                it == Ratings(total = 4, two_star = 2, one_star = 2, average = 1.5)
            }
        }
    }

    @Test
    fun `isUserWriter return correct data`() {
        withTables(UsersTable, ReviewsTable, PoemsTable) {
            val user = userDao.insertUser(givenAValidInsertUserBody())

            val review = reviewDao.insertReview(1, givenAValidInsertReviewBody())

            val isUserWriter = reviewDao.isUserWriter(review?.id!!, user?.id!!)
            val isUserWriter2 = reviewDao.isUserWriter(review.id, 243)

            assertTrue(isUserWriter)
            assertFalse(isUserWriter2)
        }
    }
}