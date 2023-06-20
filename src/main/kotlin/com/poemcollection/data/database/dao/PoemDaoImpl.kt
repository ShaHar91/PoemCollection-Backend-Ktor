package com.poemcollection.data.database.dao

import com.poemcollection.data.database.tables.*
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.models.poem.InsertOrUpdatePoem
import com.poemcollection.domain.models.poem.Poem
import com.poemcollection.domain.models.poem.PoemDetail
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import java.time.LocalDateTime

class PoemDaoImpl : IPoemDao {

    // Used this as a guide... not sure of it's correct or anything
    // https://medium.com/@pjagielski/how-we-use-kotlin-with-exposed-at-touk-eacaae4565b5
    private fun Iterable<ResultRow>.toPoem(): List<PoemDetail> {
        return (fold(mutableMapOf<Int, PoemDetail>()) { map, resultRow ->
            val poem = resultRow.toPoemDetail()
            val categoryId = resultRow.getOrNull(PoemCategoryJunctionTable.categoryId)
            val category = categoryId?.let { resultRow.toCategory() }
            val current = map.getOrDefault(poem.id, poem)
            map[poem.id] = current.copy(categories = current.categories + listOfNotNull(category))
            map
        }).values.toList()
    }

    private fun findPoemById(id: Int): PoemDetail? {
        val poemsWithAllRelations = PoemsTable innerJoin UsersTable innerJoin PoemCategoryJunctionTable innerJoin CategoriesTable
        return poemsWithAllRelations
            .select { PoemsTable.id eq id }
            .toPoem()
            .singleOrNull()
    }

    override suspend fun getPoem(id: Int): PoemDetail? =
        findPoemById(id)

    override suspend fun getPoems(categoryId: Int?): List<Poem> =
        (PoemsTable innerJoin UsersTable)
            .selectAll().toPoems()

    override suspend fun insertPoem(insertPoem: InsertOrUpdatePoem, writerId: Int): PoemDetail? {

        val id = PoemsTable.insertAndGetId {
            val time = LocalDateTime.now().toDatabaseString()

            it[title] = insertPoem.title
            it[body] = insertPoem.body
            it[this.writerId] = writerId
            it[createdAt] = time
            it[updatedAt] = time
        }.value

        insertPoem.categoryIds.forEach { catId ->
            PoemCategoryJunctionTable.insert {
                it[poemId] = id
                it[categoryId] = catId
            }
        }

        // Need to use this function to get the poem with every relation added to it!
        return findPoemById(id)
    }

    override suspend fun updatePoem(id: Int, updatePoem: InsertOrUpdatePoem): PoemDetail? {
        PoemsTable.update({ PoemsTable.id eq id }) {
            it[title] = updatePoem.title
            it[body] = updatePoem.body
        }

        // Delete the pivot rows for the categories that are not returned anymore
        PoemCategoryJunctionTable.deleteWhere { poemId eq id and (categoryId notInList updatePoem.categoryIds) }
        updatePoem.categoryIds.forEach { catId ->
            // Ignore the "UNIQUE constraint error for the 'poemId' and 'categoryId'
            PoemCategoryJunctionTable.insertIgnore {
                it[poemId] = id
                it[categoryId] = catId
            }
        }

        return findPoemById(id)
    }

    override suspend fun deletePoem(id: Int): Boolean = run {
        //TODO: check if the cascade deletion can be used instead of this!!!
        val result = PoemsTable.deleteWhere { PoemsTable.id eq id }
        val result2 = PoemCategoryJunctionTable.deleteWhere { poemId eq id }
        ReviewsTable.deleteWhere { poemId eq id }
        result > 0 && result2 > 0
    }

    override suspend fun isUserWriter(poemId: Int, userId: Int): Boolean =
        PoemsTable.select { PoemsTable.id eq poemId }.first()[PoemsTable.writerId].value == userId
}