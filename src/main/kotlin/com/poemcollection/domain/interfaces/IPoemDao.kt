package com.poemcollection.domain.interfaces

import com.poemcollection.domain.models.poem.InsertOrUpdatePoem
import com.poemcollection.domain.models.poem.Poem

interface IPoemDao {

    suspend fun getPoem(id: Int): Poem?
    suspend fun getPoems(categoryId: Int?): List<Poem>
    suspend fun insertPoem(insertPoem: InsertOrUpdatePoem, writerId: Int): Poem?

    suspend fun updatePoem(id: Int, updatePoem: InsertOrUpdatePoem): Poem?
    suspend fun deletePoem(id: Int): Boolean
    suspend fun isUserWriter(poemId: Int, userId: Int): Boolean
}