package com.poemcollection.data.database.tables

import com.poemcollection.domain.models.poem.Poem
import com.poemcollection.domain.models.poem.PoemDetail
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime

object PoemsTable : IntIdTable() {
    val title = varchar("title", 255)
    val body = mediumText("body")
    val writerId = reference("writerId", UsersTable) // --> see the link for more information https://www.baeldung.com/kotlin/exposed-persistence#3-foreign-keys
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
}

fun ResultRow.toPoemDetail() = PoemDetail(
    id = this[PoemsTable.id].value,
    title = this[PoemsTable.title],
    body = this[PoemsTable.body],
    // This nullable check IS correct, when user has been deleted this check will return NULL
    writer = if (this[UsersTable.id] != null) this.toUser() else null,
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

fun Iterable<ResultRow>.toPoems() = this.map { it.toPoemWithUser() }
//fun Iterable<ResultRow>.toPoem() = this.firstOrNull()?.toPoemWithUser()
