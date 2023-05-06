package com.poemcollection.data.local.dao

import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.data.PoemsTable
import com.poemcollection.data.ReviewsTable
import com.poemcollection.data.UsersTable
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.models.Ratings
import com.poemcollection.domain.models.review.InsertOrUpdateReview
import com.poemcollection.domain.models.review.Review
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class ReviewDaoImpl : IReviewDao {

    override suspend fun getReview(id: Int): Review? = dbQuery {
        findReviewById(id)
    }

    override suspend fun getReviews(poemId: Int?): List<Review> = dbQuery {
        val reviewWithRelations = ReviewsTable innerJoin UsersTable
        reviewWithRelations.select { ReviewsTable.poemId eq poemId }.toReviews()
    }

    private fun findReviewById(reviewId: Int): Review? {
        val reviewWithRelations = ReviewsTable innerJoin UsersTable
        return reviewWithRelations.select { ReviewsTable.id eq reviewId }.toReview()
    }

    override suspend fun insertReview(poemId: Int, insertReview: InsertOrUpdateReview): Review? = dbQuery {

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

            findReviewById(id)
        } else {
            null
        }
    }

    override suspend fun updateReview(id: Int, updateReview: InsertOrUpdateReview): Review? = dbQuery {
        val result = ReviewsTable.update({ ReviewsTable.id eq id }) {
            it[body] = updateReview.body
            it[rating] = updateReview.rating
        }

        if (result == 1) {
            findReviewById(id)
        } else {
            null
        }
    }

    override suspend fun deleteReview(id: Int): Boolean = dbQuery {
        val result = ReviewsTable.deleteWhere { ReviewsTable.id eq id }
        result == 1
    }

    override suspend fun calculateRatings(id: Int): Ratings = dbQuery {
        val reviewWithRelations = ReviewsTable innerJoin UsersTable
        val reviews = reviewWithRelations.select { ReviewsTable.poemId eq id }.toReviews()

        val grouped = reviews.groupBy { it.rating }
        val average = grouped.map { it.key * it.value.size }.sum().toDouble().div(reviews.size)

        Ratings(reviews.size, grouped[5]?.size ?: 0, grouped[4]?.size ?: 0, grouped[3]?.size ?: 0, grouped[2]?.size ?: 0, grouped[1]?.size ?: 0, if (average.isFinite()) average else 0.0)
    }

    override suspend fun isUserWriter(reviewId: Int, userId: Int): Boolean = dbQuery {
        ReviewsTable.select { ReviewsTable.id eq reviewId }.first()[ReviewsTable.userId].value == userId
    }
}