package com.poemcollection.routes

import com.poemcollection.data.mapper.toCategoryDto
import com.poemcollection.data.mapper.toInsertOrUpdateCategory
import com.poemcollection.data.remote.incoming.InsertOrUpdateCategoryDto
import com.poemcollection.data.responses.ErrorCodes
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.routes.interfaces.ICategoryRoutes
import com.poemcollection.utils.getCategoryId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class CategoryRoutesImpl(
    private val categoryDao: ICategoryDao
) : ICategoryRoutes {
    override suspend fun postCategory(call: ApplicationCall) {
        val insertCategory = call.receiveNullable<InsertOrUpdateCategoryDto>()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val newCategory = categoryDao.insertCategory(insertCategory.toInsertOrUpdateCategory())?.toCategoryDto()

        return if (newCategory != null) {
            call.respond(HttpStatusCode.Created, newCategory)
        } else {
            call.respond(HttpStatusCode.NoContent, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun getAllCategories(call: ApplicationCall) {
        val categories = categoryDao.getCategories().map { it.toCategoryDto() }

        return call.respond(HttpStatusCode.OK, categories)
    }

    override suspend fun getCategoryById(call: ApplicationCall) {
        val categoryId = call.getCategoryId() ?: return

        val category = categoryDao.getCategory(categoryId)?.toCategoryDto()

        return if (category != null) {
            call.respond(HttpStatusCode.OK, category)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun updateCategoryById(call: ApplicationCall) {
        val categoryId = call.getCategoryId() ?: return

        val updateCategory = call.receiveNullable<InsertOrUpdateCategoryDto>() ?: return

        val category = categoryDao.updateCategory(categoryId, updateCategory.toInsertOrUpdateCategory())?.toCategoryDto()

        return if (category != null) {
            call.respond(HttpStatusCode.OK, category)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun deleteCategoryById(call: ApplicationCall) {
        val categoryId = call.getCategoryId() ?: return

        val success = categoryDao.deleteCategory(categoryId)

        return if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }
}