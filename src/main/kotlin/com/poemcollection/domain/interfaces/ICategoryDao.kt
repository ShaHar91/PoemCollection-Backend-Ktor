package com.poemcollection.domain.interfaces

import com.poemcollection.domain.models.category.Category
import com.poemcollection.data.dto.requests.category.InsertOrUpdateCategory

interface ICategoryDao {

    fun getCategory(id: Int): Category?
    fun getCategories(): List<Category>
    fun insertCategory(category: InsertOrUpdateCategory): Category?

    fun updateCategory(id: Int, category: InsertOrUpdateCategory): Category?
    fun deleteCategory(id: Int): Boolean
    fun getListOfExistingCategoryIds(categoryIds: List<Int>): List<Int>
}