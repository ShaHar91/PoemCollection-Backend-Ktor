package com.poemcollection.routes

import com.poemcollection.data.mapper.toInsertOrUpdatePoem
import com.poemcollection.data.mapper.toPoemDto
import com.poemcollection.data.remote.incoming.poem.InsertOrUpdatePoemDto
import com.poemcollection.data.responses.ErrorCodes
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.routes.ParamConstants.CATEGORY_ID_KEY
import com.poemcollection.routes.interfaces.IPoemRoutes
import com.poemcollection.utils.getPoemId
import com.poemcollection.utils.getUserId
import com.poemcollection.utils.receiveOrRespondWithError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class PoemRoutesImpl(
    private val userDao: IUserDao,
    private val poemDao: IPoemDao,
    private val categoryDao: ICategoryDao,
    private val reviewDao: IReviewDao
) : IPoemRoutes {
    override suspend fun postPoem(call: ApplicationCall) {
        val userId = call.getUserId() ?: return

        val insertPoem = call.receiveOrRespondWithError<InsertOrUpdatePoemDto>() ?: return

        val categoryIds = categoryDao.getListOfExistingCategoryIds(insertPoem.categoryIds)
        if (categoryIds.count() != insertPoem.categoryIds.count()) {
            val nonExistingIds = insertPoem.categoryIds.filterNot { categoryIds.contains(it) }
            return call.respondText("The following categories do not exist: ${nonExistingIds.joinToString { it.toString() }}")
        }

        val newPoem = poemDao.insertPoem(insertPoem.toInsertOrUpdatePoem(), userId)?.toPoemDto()

        return if (newPoem != null) {
            call.respond(HttpStatusCode.Created, newPoem)
        } else {
            call.respond(HttpStatusCode.NoContent, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun getAllPoems(call: ApplicationCall) {
        val categoryId = call.request.queryParameters[CATEGORY_ID_KEY]?.toIntOrNull() // null is allowed!

        val poems = poemDao.getPoems(categoryId).map { it.toPoemDto() }
        return call.respond(HttpStatusCode.OK, poems)
    }

    override suspend fun getPoemById(call: ApplicationCall) {
        val poemId = call.getPoemId() ?: return

        val poem = poemDao.getPoem(poemId)?.toPoemDto()

        //TODO: maybe get a couple of things in a collection so the app doesn't have to do 3 seperate calls?
        // e.g. { "poem": {}, "ratings" : {}, "ownReview": {}, "reviews": {}} ----> where reviews are limited to 3 or 5 reviews...

        return if (poem != null) {
            call.respond(HttpStatusCode.OK, poem)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun updatePoemById(call: ApplicationCall) {
        val poemId = call.getPoemId() ?: return
        val userId = call.getUserId() ?: return

        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = poemDao.isUserWriter(poemId, userId)

        if (!isUserWriter && !isUserAdmin) return call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidPermissionsToUpdatePoem.asResponse)

        val updatePoem = call.receiveOrRespondWithError<InsertOrUpdatePoemDto>() ?: return

        val categoryIds = categoryDao.getListOfExistingCategoryIds(updatePoem.categoryIds)
        if (categoryIds.count() != updatePoem.categoryIds.count()) {
            val nonExistingIds = updatePoem.categoryIds.filterNot { categoryIds.contains(it) }
            return call.respondText("The following categories do not exist: ${nonExistingIds.joinToString { it.toString() }}")
        }

        val poem = poemDao.updatePoem(poemId, updatePoem.toInsertOrUpdatePoem())?.toPoemDto()

        return if (poem != null) {
            call.respond(HttpStatusCode.OK, poem)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun deletePoemById(call: ApplicationCall) {
        val poemId = call.getPoemId() ?: return
        val userId = call.getUserId() ?: return

        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = poemDao.isUserWriter(poemId, userId)

        if (!isUserWriter && !isUserAdmin) return call.respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidPermissionsToDeletePoem.asResponse)

        val success = poemDao.deletePoem(poemId)

        return if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound, ErrorCodes.ErrorResourceNotFound.asResponse)
        }
    }

    override suspend fun getRatingsForPoem(call: ApplicationCall) {
        val poemId = call.getPoemId() ?: return

        val ratings = reviewDao.calculateRatings(poemId)

        return call.respond(HttpStatusCode.OK, ratings)
    }
}