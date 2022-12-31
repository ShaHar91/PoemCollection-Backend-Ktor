package com.poemcollection.data.dao

import com.poemcollection.data.Categories
import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.data.PoemCategoryJunction
import com.poemcollection.data.Poems
import com.poemcollection.data.Users
import com.poemcollection.data.models.*
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime

class PoemDaoImpl : IPoemDao {

    private fun resultRowToPoem(row: ResultRow) = Poem(
        id = row[Poems.id].value,
        title = row[Poems.title],
        body = row[Poems.body],
        createdAt = row[Poems.createdAt],
        updatedAt = row[Poems.updatedAt]
    )

    private fun resultRowToPoemWithUser(row: ResultRow) = Poem(
        id = row[Poems.id].value,
        title = row[Poems.title],
        body = row[Poems.body],
        writer = resultRowToUser(row),
        createdAt = row[Poems.createdAt],
        updatedAt = row[Poems.updatedAt]
    )

    private fun resultRowToUser(row: ResultRow) = User(
        userId = row[Users.id].value,
        firstName = row[Users.firstName],
        lastName = row[Users.lastName],
        email = row[Users.email],
        createdAt = row[Users.createdAt],
        updatedAt = row[Users.updatedAt]
    )

    private fun resultRowToCategory(row: ResultRow) = Category(
        id = row[Categories.id].value,
        name = row[Categories.name],
        createdAt = row[Categories.createdAt],
        updatedAt = row[Categories.updatedAt]
    )

    override suspend fun getPoem(id: Int): Poem? = dbQuery {
        val poemsWithAllRelations = Poems innerJoin Users innerJoin PoemCategoryJunction innerJoin Categories
        poemsWithAllRelations
            .select { Poems.id eq id }
            .toPoems()
            .singleOrNull()
    }

    // Used this as a guide... not sure of it's correct or anything
    // https://medium.com/@pjagielski/how-we-use-kotlin-with-exposed-at-touk-eacaae4565b5
    private fun Iterable<ResultRow>.toPoems(): List<Poem> {
        return (fold(mutableMapOf<Int, Poem>()) { map, resultRow ->
            val poem = resultRowToPoemWithUser(resultRow)
            val categoryId = resultRow.getOrNull(PoemCategoryJunction.categoryId)
            val category = categoryId?.let { resultRowToCategory(resultRow) }
            val current = map.getOrDefault(poem.id, poem)
            map[poem.id] = current.copy(categories = current.categories + listOfNotNull(category))
            map
        }).values.toList()
    }

    override suspend fun getPoems(categoryId: Int?): List<Poem> = dbQuery {
        val poemsWithAllRelations = Poems innerJoin Users innerJoin PoemCategoryJunction innerJoin Categories
        poemsWithAllRelations
//            .select { categoryId?.let { PoemCategoryJunction.categoryId eq categoryId } ?: Op.TRUE }
            .let {
                if (categoryId == null) {
                    it.selectAll()
                } else {
                    it.select { PoemCategoryJunction.categoryId eq categoryId }
                }
            }
            .toPoems()
    }

    override suspend fun insertPoem(insertPoem: InsertPoem): Int? = dbQuery {

        val poem = Poems.insertAndGetId {
            it[title] = insertPoem.title
            it[body] = insertPoem.body
            it[writerId] = insertPoem.writerId
            it[createdAt] = LocalDateTime.now().toDatabaseString()
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        insertPoem.categoryIds.forEach { catId ->
            PoemCategoryJunction.insert {
                it[poemId] = poem.value
                it[categoryId] = catId
            }
        }

        poem.value
    }

    override suspend fun updatePoem(id: Int, updatePoem: UpdatePoem): Poem? {
        TODO("Not yet implemented")
    }

    override suspend fun deletePoem(id: Int): Boolean {
        TODO("Not yet implemented")
    }
}