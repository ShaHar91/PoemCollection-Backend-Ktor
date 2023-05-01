package com.poemcollection.domain.interfaces

import com.poemcollection.domain.models.InsertPoem
import com.poemcollection.domain.models.Poem
import com.poemcollection.domain.models.UpdatePoem

interface IPoemDao {

    suspend fun getPoem(id: Int): Poem?
    suspend fun getPoems(categoryId: Int?): List<Poem>
    suspend fun insertPoem(insertPoem: InsertPoem): Poem?

    suspend fun updatePoem(id: Int, updatePoem: UpdatePoem): Poem?
    suspend fun deletePoem(id: Int): Boolean
    suspend fun isUserWriter(poemId: Int, userId: Int): Boolean
}