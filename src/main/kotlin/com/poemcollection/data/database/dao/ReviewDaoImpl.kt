package com.poemcollection.data.database.dao

import com.poemcollection.data.database.PoemsTable
import com.poemcollection.data.database.ReviewsTable
import com.poemcollection.data.database.UsersTable
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.models.Ratings
import com.poemcollection.domain.models.review.InsertOrUpdateReview
import com.poemcollection.domain.models.review.Review
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class ReviewDaoImpl : IReviewDao {

    override suspend fun getReview(id: Int): Review? =
        findReviewById(id)

    override suspend fun getReviews(poemId: Int?, limit: Int?): List<Review> =
        (ReviewsTable innerJoin UsersTable)
            .select { ReviewsTable.poemId eq poemId }.also {
                limit ?: return@also
                it.limit(limit)
            }.toReviews()

    private fun findReviewById(reviewId: Int): Review? =
        (ReviewsTable innerJoin UsersTable)
            .select { ReviewsTable.id eq reviewId }.toReview()

    override suspend fun insertReview(poemId: Int, insertReview: InsertOrUpdateReview): Review? = run {

        val existsOp = exists(PoemsTable.select { PoemsTable.id eq poemId })
        val result = Table.Dual.slice(existsOp).selectAll().first()
        val existsResult = result[existsOp]

        //TODO: user can only create 1 review per poem!!

        if (existsResult) {
            val id = ReviewsTable.insertAndGetId {
                it[body] = insertReview.body
                it[rating] = insertReview.rating
                it[userId] = insertReview.userId
                it[this.poemId] = poemId
                it[createdAt] = LocalDateTime.now().toDatabaseString()
                it[updatedAt] = LocalDateTime.now().toDatabaseString()
            }.value

            // Need to use this function to get the review with every relation added to it!
            findReviewById(id)
        } else {
            null
        }
    }

    override suspend fun updateReview(id: Int, updateReview: InsertOrUpdateReview): Review? {
        ReviewsTable.update({ ReviewsTable.id eq id }) {
            it[body] = updateReview.body
            it[rating] = updateReview.rating
        }

        return findReviewById(id)
    }

    override suspend fun deleteReview(id: Int): Boolean =
        ReviewsTable.deleteWhere { ReviewsTable.id eq id } > 0

    override suspend fun calculateRatings(id: Int): Ratings {
        val reviewWithRelations = ReviewsTable innerJoin UsersTable
        val reviews = reviewWithRelations.select { ReviewsTable.poemId eq id }.toReviews()

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

    override suspend fun isUserWriter(reviewId: Int, userId: Int): Boolean =
        ReviewsTable.select { ReviewsTable.id eq reviewId }.first()[ReviewsTable.userId].value == userId
}