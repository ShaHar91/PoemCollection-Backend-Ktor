package com.poemcollection.data.database.dao

import com.poemcollection.data.database.tables.*
import com.poemcollection.data.dto.requests.poem.InsertOrUpdatePoem
import com.poemcollection.domain.interfaces.IPoemDao
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
        // Using "leftJoin UsersTable" because we want to find poems even though the user has been removed...
        val poemsWithAllRelations = PoemsTable leftJoin UsersTable innerJoin PoemCategoryJunctionTable innerJoin CategoriesTable
        return poemsWithAllRelations
            .select { PoemsTable.id eq id }
            .toPoem()
            .singleOrNull()
    }

    override fun getPoem(id: Int): PoemDetail? =
        findPoemById(id)

    override fun getPoems(categoryId: Int?): List<Poem> =
    // Using "leftJoin UsersTable" because we want to find poems even though the user has been removed...
        //TODO: use the "categoryId" to fetch the list of poems for a specific category!
        (PoemsTable leftJoin UsersTable)
            .selectAll().toPoems()

    override fun insertPoem(insertPoem: InsertOrUpdatePoem, writerId: Int): PoemDetail? {

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

    override fun updatePoem(id: Int, updatePoem: InsertOrUpdatePoem): PoemDetail? {
        PoemsTable.update({ PoemsTable.id eq id }) {
            it[title] = updatePoem.title
            it[body] = updatePoem.body
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
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

    override fun deletePoem(id: Int): Boolean = run {
        //TODO: check if the cascade deletion can be used instead of this!!!
        val result = PoemsTable.deleteWhere { PoemsTable.id eq id }
        val result2 = PoemCategoryJunctionTable.deleteWhere { poemId eq id }
        ReviewsTable.deleteWhere { poemId eq id }
        result > 0 && result2 > 0
    }

    override fun isUserWriter(poemId: Int, userId: Int): Boolean =
        PoemsTable.select { PoemsTable.id eq poemId }.first()[PoemsTable.writerId].value == userId
}