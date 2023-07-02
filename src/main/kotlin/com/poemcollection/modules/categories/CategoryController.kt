package com.poemcollection.modules.categories

import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.models.category.Category
import com.poemcollection.data.dto.requests.category.InsertOrUpdateCategory
import com.poemcollection.modules.BaseController
import com.poemcollection.utils.TBDException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CategoryControllerImpl : BaseController(), CategoryController, KoinComponent {

    private val categoryDao by inject<ICategoryDao>()

    override suspend fun postCategory(insertCategory: InsertOrUpdateCategory): Category = dbQuery {
        categoryDao.insertCategory(insertCategory) ?: throw TBDException
    }

    override suspend fun getAllCategories(): List<Category> = dbQuery {
        categoryDao.getCategories()
    }

    override suspend fun getCategoryById(categoryId: Int): Category = dbQuery {
        categoryDao.getCategory(categoryId) ?: throw TBDException
    }

    override suspend fun updateCategoryById(categoryId: Int, updateCategory: InsertOrUpdateCategory): Category = dbQuery {
        categoryDao.updateCategory(categoryId, updateCategory) ?: throw TBDException
    }

    override suspend fun deleteCategoryById(categoryId: Int) {
        dbQuery {
            categoryDao.deleteCategory(categoryId)
        }
    }
}

interface CategoryController {
    suspend fun postCategory(insertCategory: InsertOrUpdateCategory): Category
    suspend fun getAllCategories(): List<Category>
    suspend fun getCategoryById(categoryId: Int): Category
    suspend fun updateCategoryById(categoryId: Int, updateCategory: InsertOrUpdateCategory): Category
    suspend fun deleteCategoryById(categoryId: Int)
}