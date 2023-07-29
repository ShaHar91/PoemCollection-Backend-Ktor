package com.poemcollection.modules.categories

import com.poemcollection.data.dto.requests.category.CategoryDto
import com.poemcollection.data.dto.requests.category.InsertOrUpdateCategory
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.models.category.toDto
import com.poemcollection.modules.BaseController
import com.poemcollection.statuspages.*

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CategoryControllerImpl : BaseController(), CategoryController, KoinComponent {

    private val categoryDao by inject<ICategoryDao>()

    override suspend fun postCategory(insertCategory: InsertOrUpdateCategory): CategoryDto = dbQuery {
        // Can't have a category with the same name
        categoryDao.getCategoryByName(insertCategory.name)?.let {
            throw ErrorDuplicateEntity
        }
        val category = categoryDao.insertCategory(insertCategory)

        category?.toDto() ?: throw ErrorFailedCreate
    }

    override suspend fun getAllCategories(): List<CategoryDto> = dbQuery {
        categoryDao.getCategories().map { it.toDto() }
    }

    override suspend fun getCategoryById(categoryId: Int): CategoryDto = dbQuery {
        categoryDao.getCategory(categoryId)?.toDto() ?: throw ErrorNotFound
    }

    override suspend fun updateCategoryById(categoryId: Int, updateCategory: InsertOrUpdateCategory): CategoryDto = dbQuery {
        categoryDao.updateCategory(categoryId, updateCategory)?.toDto() ?: throw ErrorFailedUpdate
    }

    override suspend fun deleteCategoryById(categoryId: Int) {
        dbQuery {
            val deleted = categoryDao.deleteCategory(categoryId)
            if (!deleted) throw ErrorFailedDelete
        }
    }
}

interface CategoryController {
    suspend fun postCategory(insertCategory: InsertOrUpdateCategory): CategoryDto
    suspend fun getAllCategories(): List<CategoryDto>
    suspend fun getCategoryById(categoryId: Int): CategoryDto
    suspend fun updateCategoryById(categoryId: Int, updateCategory: InsertOrUpdateCategory): CategoryDto
    suspend fun deleteCategoryById(categoryId: Int)
}