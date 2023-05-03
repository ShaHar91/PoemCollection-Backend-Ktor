package com.poemcollection.routes

import com.poemcollection.data.requests.InsertOrUpdateCategoryReq
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
        val insertCategory = call.receiveNullable<InsertOrUpdateCategoryReq>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        val newCategory = categoryDao.insertCategory(insertCategory)

        if (newCategory != null) {
            call.respond(HttpStatusCode.Created, newCategory)
        } else {
            call.respond(HttpStatusCode.NoContent, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun getAllCategories(call: ApplicationCall) {
        val categories = categoryDao.getCategories()

        call.respond(HttpStatusCode.OK, categories)
    }

    override suspend fun getCategoryById(call: ApplicationCall) {
        val categoryId = call.getCategoryId() ?: return

        val category = categoryDao.getCategory(categoryId)

        if (category != null) {
            call.respond(HttpStatusCode.OK, category)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun updateCategoryById(call: ApplicationCall) {
        val categoryId = call.getCategoryId() ?: return

        val updateCategory = call.receive<InsertOrUpdateCategoryReq>()

        val category = categoryDao.updateCategory(categoryId, updateCategory)

        if (category != null) {
            call.respond(HttpStatusCode.OK, category)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun deleteCategoryById(call: ApplicationCall) {
        val categoryId = call.getCategoryId() ?: return

        val success = categoryDao.deleteCategory(categoryId)

        if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }
}