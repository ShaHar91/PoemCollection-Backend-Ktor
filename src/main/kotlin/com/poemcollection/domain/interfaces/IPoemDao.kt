package com.poemcollection.domain.interfaces

import com.poemcollection.domain.models.poem.InsertOrUpdatePoem
import com.poemcollection.domain.models.poem.Poem
import com.poemcollection.domain.models.poem.PoemDetail

interface IPoemDao {

    suspend fun getPoem(id: Int): PoemDetail?
    suspend fun getPoems(categoryId: Int?): List<Poem>
    suspend fun insertPoem(insertPoem: InsertOrUpdatePoem, writerId: Int): PoemDetail?

    suspend fun updatePoem(id: Int, updatePoem: InsertOrUpdatePoem): PoemDetail?
    suspend fun deletePoem(id: Int): Boolean
    suspend fun isUserWriter(poemId: Int, userId: Int): Boolean
}