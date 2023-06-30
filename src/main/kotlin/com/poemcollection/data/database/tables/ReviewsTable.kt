package com.poemcollection.data.database.tables

import com.poemcollection.domain.models.review.Review
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime

object ReviewsTable : IntIdTable() {
    val body = mediumText("body")
    val rating = integer("rating").default(0)
    val userId = reference("userId", UsersTable, onDelete = ReferenceOption.SET_NULL) // As long as this foreign key references the primary key of the Users table this is enough.
    val poemId = reference("poemId", PoemsTable, onDelete = ReferenceOption.CASCADE) // As long as this foreign key references the primary key of the Poems table this is enough.
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())

    init {
        // Only a single pair can exist, duplicates are not allowed/necessary
        uniqueIndex(userId, poemId)
    }
}

fun ResultRow.toReviewWithUser() = Review(
    id = this[ReviewsTable.id].value,
    body = this[ReviewsTable.body],
    rating = this[ReviewsTable.rating],
    user = this.toUser(),
    createdAt = this[ReviewsTable.createdAt],
    updatedAt = this[ReviewsTable.updatedAt]
)

fun Iterable<ResultRow>.toReviews() = this.map { it.toReviewWithUser() }
fun Iterable<ResultRow>.toReview() = this.firstOrNull()?.toReviewWithUser()

