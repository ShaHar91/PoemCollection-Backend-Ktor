package com.poemcollection.data.database.dao

import com.poemcollection.data.database.tables.ReviewsTable
import com.poemcollection.data.database.tables.UsersTable
import com.poemcollection.data.database.tables.toReview
import com.poemcollection.data.database.tables.toReviews
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.models.Ratings
import com.poemcollection.data.dto.requests.review.InsertOrUpdateReview
import com.poemcollection.domain.models.review.Review
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class ReviewDaoImpl : IReviewDao {

    override fun getReview(id: Int): Review? =
        findReviewById(id)

    // TODO: maybe use something like `excludedUserId`?? 😅
    override fun getReviews(poemId: Int?, limit: Int?): List<Review> =
        (ReviewsTable innerJoin UsersTable)
            .select { ReviewsTable.poemId eq poemId }.also {
                limit ?: return@also
                it.limit(limit)
            }.toReviews()

    private fun findReviewById(reviewId: Int): Review? =
        (ReviewsTable innerJoin UsersTable)
            .select { ReviewsTable.id eq reviewId }.toReview()

    override fun insertReview(poemId: Int, insertReview: InsertOrUpdateReview): Review? = run {
        val id = ReviewsTable.insertAndGetId {
            val time = LocalDateTime.now().toDatabaseString()

            it[body] = insertReview.body
            it[rating] = insertReview.rating
            it[userId] = insertReview.userId
            it[this.poemId] = poemId
            it[createdAt] = time
            it[updatedAt] = time
        }.value

        // Need to use this function to get the review with every relation added to it!
        findReviewById(id)
    }

    override fun updateReview(id: Int, updateReview: InsertOrUpdateReview): Review? {
        ReviewsTable.update({ ReviewsTable.id eq id }) {
            it[body] = updateReview.body
            it[rating] = updateReview.rating
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        return findReviewById(id)
    }

    override fun deleteReview(id: Int): Boolean =
        ReviewsTable.deleteWhere { ReviewsTable.id eq id } > 0

    override fun calculateRatings(poemId: Int): Ratings {
        val reviewWithRelations = ReviewsTable innerJoin UsersTable
        val reviews = reviewWithRelations.select { ReviewsTable.poemId eq poemId }.toReviews()

        val grouped = reviews.groupBy { it.rating }
        val average = grouped.map { it.key * it.value.size }.sum().toDouble().div(reviews.size)

        return Ratings(
            reviews.size,
            grouped[5]?.size ?: 0,
            grouped[4]?.size ?: 0,
            grouped[3]?.size ?: 0,
            grouped[2]?.size ?: 0,
            grouped[1]?.size ?: 0,
            if (average.isFinite()) average else 0.0
        )
    }

    override fun isUserWriter(reviewId: Int, userId: Int): Boolean =
        ReviewsTable.select { ReviewsTable.id eq reviewId }.first()[ReviewsTable.userId].value == userId
}