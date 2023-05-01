package com.poemcollection.routes

import com.poemcollection.data.requests.InsertOrUpdateCategoryReq
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.routes.ParamConstants.CATEGORY_ID_KEY
import com.poemcollection.routes.interfaces.ICategoryRoutes
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
            call.respondText("Not created", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getAllCategories(call: ApplicationCall) {
        val categories = categoryDao.getCategories()

        call.respond(HttpStatusCode.OK, categories)
    }

    override suspend fun getCategoryById(call: ApplicationCall) {
        val id = call.parameters[CATEGORY_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val category = categoryDao.getCategory(id)

        if (category != null) {
            call.respond(HttpStatusCode.OK, category)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun updateCategoryById(call: ApplicationCall) {
        val id = call.parameters[CATEGORY_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val updateCategory = call.receive<InsertOrUpdateCategoryReq>()

        val category = categoryDao.updateCategory(id, updateCategory)

        if (category != null) {
            call.respond(HttpStatusCode.OK, category)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun deleteCategoryById(call: ApplicationCall) {
        val id = call.parameters[CATEGORY_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val success = categoryDao.deleteCategory(id)

        if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }
}