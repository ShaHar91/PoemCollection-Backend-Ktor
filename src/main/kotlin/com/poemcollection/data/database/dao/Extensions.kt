package com.poemcollection.data.database.dao

import com.poemcollection.data.database.CategoriesTable
import com.poemcollection.data.database.PoemsTable
import com.poemcollection.data.database.ReviewsTable
import com.poemcollection.data.database.UsersTable
import com.poemcollection.domain.models.category.Category
import com.poemcollection.domain.models.poem.Poem
import com.poemcollection.domain.models.poem.PoemDetail
import com.poemcollection.domain.models.review.Review
import com.poemcollection.domain.models.user.User
import com.poemcollection.domain.models.user.UserHashable
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toPoemDetail() = PoemDetail(
    id = this[PoemsTable.id].value,
    title = this[PoemsTable.title],
    body = this[PoemsTable.body],
    writer = this.toUser(),
    createdAt = this[PoemsTable.createdAt],
    updatedAt = this[PoemsTable.updatedAt]
)

fun ResultRow.toPoemWithUser() = Poem(
    id = this[PoemsTable.id].value,
    title = this[PoemsTable.title],
    writer = this.toUser(),
    createdAt = this[PoemsTable.createdAt],
    updatedAt = this[PoemsTable.updatedAt]
)

fun ResultRow.toUser() = User(
    id = this[UsersTable.id].value,
    firstName = this[UsersTable.firstName],
    lastName = this[UsersTable.lastName],
    email = this[UsersTable.email],
    createdAt = this[UsersTable.createdAt],
    updatedAt = this[UsersTable.updatedAt],
    role = this[UsersTable.role]
)

fun ResultRow.toCategory() = Category(
    id = this[CategoriesTable.id].value,
    name = this[CategoriesTable.name],
    createdAt = this[CategoriesTable.createdAt],
    updatedAt = this[CategoriesTable.updatedAt]
)

fun ResultRow.toReviewWithUser() = Review(
    id = this[ReviewsTable.id].value,
    body = this[ReviewsTable.body],
    rating = this[ReviewsTable.rating],
    user = this.toUser(),
    createdAt = this[ReviewsTable.createdAt],
    updatedAt = this[ReviewsTable.updatedAt]
)

fun ResultRow.toUserHashable() = UserHashable(
    id = this[UsersTable.id].value,
    email = this[UsersTable.email],
    password = this[UsersTable.password],
    salt = this[UsersTable.salt]
)

fun Iterable<ResultRow>.toCategories() = this.map { it.toCategory() }
fun Iterable<ResultRow>.toCategory() = this.firstOrNull()?.toCategory()

fun Iterable<ResultRow>.toUsers() = this.map { it.toUser() }
fun Iterable<ResultRow>.toUser() = this.firstOrNull()?.toUser()
fun Iterable<ResultRow>.toUserHashable() = this.firstOrNull()?.toUserHashable()

fun Iterable<ResultRow>.toPoems() = this.map { it.toPoemWithUser() }
fun Iterable<ResultRow>.toPoem() = this.firstOrNull()?.toPoemWithUser()

fun Iterable<ResultRow>.toReviews() = this.map { it.toReviewWithUser() }
fun Iterable<ResultRow>.toReview() = this.firstOrNull()?.toReviewWithUser()

