package com.poemcollection.domain.interfaces

import com.poemcollection.data.models.Category
import com.poemcollection.data.models.InsertOrUpdateCategory

interface ICategoryDao {

    suspend fun getCategory(id: Int): Category?
    suspend fun getCategories(): List<Category>
    suspend fun insertCategory(category: InsertOrUpdateCategory): Category?

    suspend fun updateCategory(id: Int, category: InsertOrUpdateCategory): Category?
    suspend fun deleteCategory(id: Int): Boolean
}