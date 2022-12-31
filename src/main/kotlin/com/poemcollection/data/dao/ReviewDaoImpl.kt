package com.poemcollection.data.dao

import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.data.Poems
import com.poemcollection.data.Reviews
import com.poemcollection.data.Users
import com.poemcollection.data.models.InsertOrUpdateReview
import com.poemcollection.data.models.Ratings
import com.poemcollection.data.models.Review
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class ReviewDaoImpl : IReviewDao {

    override suspend fun getReview(id: Int): Review? = dbQuery {
        findReviewById(id)
    }

    override suspend fun getReviews(poemId: Int?): List<Review> = dbQuery {
        val reviewWithRelations = Reviews innerJoin Users
        reviewWithRelations.select { Reviews.poemId eq poemId }.toReviews()
    }

    private fun findReviewById(reviewId: Int): Review? {
        val reviewWithRelations = Reviews innerJoin Users
        return reviewWithRelations.select { Reviews.id eq reviewId }.toReview()
    }

    override suspend fun insertReview(pId: Int, insertReview: InsertOrUpdateReview): Review? = dbQuery {

        val existsOp = exists(Poems.select { Poems.id eq pId })
        val result = Table.Dual.slice(existsOp).selectAll().first()
        val existsResult = result[existsOp]

        //TODO: user can only create 1 review per poem!!

        if (existsResult) {
            val id = Reviews.insertAndGetId {
                it[body] = insertReview.body
                it[rating] = insertReview.rating
                it[userId] = insertReview.userId
                it[poemId] = pId
                it[createdAt] = LocalDateTime.now().toDatabaseString()
                it[updatedAt] = LocalDateTime.now().toDatabaseString()
            }.value

            findReviewById(id)
        } else {
            null
        }
    }

    override suspend fun updateReview(id: Int, updateReview: InsertOrUpdateReview): Review? = dbQuery {
        val result = Reviews.update({ Reviews.id eq id }) {
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
        val result = Reviews.deleteWhere { Reviews.id eq id }
        result == 1
    }

    override suspend fun calculateRatings(id: Int): Ratings = dbQuery {
        val reviewWithRelations = Reviews innerJoin Users
        val reviews = reviewWithRelations.select { Reviews.poemId eq id }.toReviews()

        val grouped = reviews.groupBy { it.rating }
        val average = grouped.map { it.key * it.value.size }.sum().toDouble().div(reviews.size)


        Ratings(reviews.size, grouped[5]?.size ?: 0, grouped[4]?.size ?: 0, grouped[3]?.size ?: 0, grouped[2]?.size ?: 0, grouped[1]?.size ?: 0, average)
    }
}