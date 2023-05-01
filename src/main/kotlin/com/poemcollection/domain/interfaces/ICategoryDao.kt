package com.poemcollection.domain.interfaces

import com.poemcollection.data.requests.InsertOrUpdateCategoryReq
import com.poemcollection.domain.models.Category

interface ICategoryDao {

    suspend fun getCategory(id: Int): Category?
    suspend fun getCategories(): List<Category>
    suspend fun insertCategory(category: InsertOrUpdateCategoryReq): Category?

    suspend fun updateCategory(id: Int, category: InsertOrUpdateCategoryReq): Category?
    suspend fun deleteCategory(id: Int): Boolean
    suspend fun getListOfExistingCategoryIds(categoryIds: List<Int>): List<Int>
}