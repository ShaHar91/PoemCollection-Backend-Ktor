package com.poemcollection.domain.interfaces

import com.poemcollection.data.dto.requests.poem.InsertOrUpdatePoem
import com.poemcollection.domain.models.poem.Poem
import com.poemcollection.domain.models.poem.PoemDetail

interface IPoemDao {

    fun getPoem(id: Int): PoemDetail?
    fun getPoems(categoryId: Int? = null): List<Poem>
    fun insertPoem(insertPoem: InsertOrUpdatePoem, writerId: Int): PoemDetail?

    fun updatePoem(id: Int, updatePoem: InsertOrUpdatePoem): PoemDetail?
    fun deletePoem(id: Int): Boolean
    fun isUserWriter(poemId: Int, userId: Int): Boolean
}