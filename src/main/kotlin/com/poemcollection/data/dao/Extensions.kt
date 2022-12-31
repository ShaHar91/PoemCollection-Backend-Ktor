package com.poemcollection.data.dao

import com.poemcollection.data.Categories
import com.poemcollection.data.Poems
import com.poemcollection.data.Users
import com.poemcollection.data.models.Category
import com.poemcollection.data.models.Poem
import com.poemcollection.data.models.User
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toPoemWithUser() = Poem(
    id = this[Poems.id].value,
    title = this[Poems.title],
    body = this[Poems.body],
    writer = this.toUser(),
    createdAt = this[Poems.createdAt],
    updatedAt = this[Poems.updatedAt]
)

fun ResultRow.toUser() = User(
    userId = this[Users.id].value,
    firstName = this[Users.firstName],
    lastName = this[Users.lastName],
    email = this[Users.email],
    createdAt = this[Users.createdAt],
    updatedAt = this[Users.updatedAt]
)

fun ResultRow.toCategory() = Category(
    id = this[Categories.id].value,
    name = this[Categories.name],
    createdAt = this[Categories.createdAt],
    updatedAt = this[Categories.updatedAt]
)

fun Iterable<ResultRow>.toCategories() = this.map { it.toCategory() }
fun Iterable<ResultRow>.toUsers() = this.map { it.toUser() }

fun Query.toCategory() = this.map { it.toCategory() }
fun Query.toUser() = this.map { it.toUser() }

