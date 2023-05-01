package com.poemcollection.data.dao

import com.poemcollection.data.*
import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.models.InsertPoem
import com.poemcollection.domain.models.Poem
import com.poemcollection.domain.models.UpdatePoem
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import java.time.LocalDateTime

class PoemDaoImpl : IPoemDao {

    // Used this as a guide... not sure of it's correct or anything
    // https://medium.com/@pjagielski/how-we-use-kotlin-with-exposed-at-touk-eacaae4565b5
    private fun Iterable<ResultRow>.toPoems(): List<Poem> {
        return (fold(mutableMapOf<Int, Poem>()) { map, resultRow ->
            val poem = resultRow.toPoemWithUser()
            val categoryId = resultRow.getOrNull(PoemCategoryJunctionTable.categoryId)
            val category = categoryId?.let { resultRow.toCategory() }
            val current = map.getOrDefault(poem.id, poem)
            map[poem.id] = current.copy(categories = current.categories + listOfNotNull(category))
            map
        }).values.toList()
    }

    private fun findPoemById(id: Int): Poem? {
        val poemsWithAllRelations = PoemsTable innerJoin UsersTable innerJoin PoemCategoryJunctionTable innerJoin CategoriesTable
        return poemsWithAllRelations
            .select { PoemsTable.id eq id }
            .toPoems()
            .singleOrNull()
    }

    override suspend fun getPoem(id: Int): Poem? = dbQuery {
        findPoemById(id)
    }

    override suspend fun getPoems(categoryId: Int?): List<Poem> = dbQuery {
        val poemsWithAllRelations = PoemsTable innerJoin UsersTable innerJoin PoemCategoryJunctionTable innerJoin CategoriesTable
        poemsWithAllRelations
//            .select { categoryId?.let { PoemCategoryJunction.categoryId eq categoryId } ?: Op.TRUE }
            .let {
                if (categoryId == null) {
                    it.selectAll()
                } else {
                    it.select { PoemCategoryJunctionTable.categoryId eq categoryId }
                }
            }
            .toPoems()
    }

    override suspend fun insertPoem(insertPoem: InsertPoem): Poem? = dbQuery {

        val id = PoemsTable.insertAndGetId {
            it[title] = insertPoem.title
            it[body] = insertPoem.body
            it[writerId] = insertPoem.writerId
            it[createdAt] = LocalDateTime.now().toDatabaseString()
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }.value

        insertPoem.categoryIds.forEach { catId ->
            PoemCategoryJunctionTable.insert {
                it[poemId] = id
                it[categoryId] = catId
            }
        }

        findPoemById(id)
    }

    override suspend fun updatePoem(id: Int, updatePoem: UpdatePoem): Poem? = dbQuery {
        val result = PoemsTable.update({ PoemsTable.id eq id }) {
            it[title] = updatePoem.title
            it[body] = updatePoem.body
        }

        // Delete the pivot rows for the categories that are not returned anymored
        PoemCategoryJunctionTable.deleteWhere { poemId eq id and (categoryId notInList updatePoem.categoryIds) }
        updatePoem.categoryIds.forEach { catId ->
            // Ignore the "UNIQUE constraint error for the 'poemId' and 'categoryId'
            PoemCategoryJunctionTable.insertIgnore {
                it[poemId] = id
                it[categoryId] = catId
            }
        }

        if (result == 1) {
            findPoemById(id)
            val poemsWithAllRelations = PoemsTable innerJoin UsersTable innerJoin PoemCategoryJunctionTable innerJoin CategoriesTable
            poemsWithAllRelations.select { PoemsTable.id eq id }.toPoems().singleOrNull()
        } else {
            null
        }
    }

    override suspend fun deletePoem(id: Int): Boolean = dbQuery {
        //TODO: check if the cascade deletion can be used instead of this!!!
        val result = PoemsTable.deleteWhere { PoemsTable.id eq id }
        val result2 = PoemCategoryJunctionTable.deleteWhere { poemId eq id }
        ReviewsTable.deleteWhere { poemId eq id }
        result >= 1 && result2 >= 1
    }

    override suspend fun isUserWriter(poemId: Int, userId: Int): Boolean = dbQuery {
        PoemsTable.select { PoemsTable.id eq poemId }.first()[PoemsTable.writerId].value == userId
    }
}