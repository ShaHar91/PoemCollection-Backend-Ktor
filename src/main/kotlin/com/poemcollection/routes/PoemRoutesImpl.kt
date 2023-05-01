package com.poemcollection.routes

import com.poemcollection.data.models.InsertPoem
import com.poemcollection.data.models.UpdatePoem
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.routes.ParamConstants.CATEGORY_ID_KEY
import com.poemcollection.routes.ParamConstants.POEM_ID_KEY
import com.poemcollection.routes.interfaces.IPoemRoutes
import com.poemcollection.utils.getPoemId
import com.poemcollection.utils.getUserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class PoemRoutesImpl(
    private val userDao: IUserDao,
    private val poemDao: IPoemDao,
    private val reviewDao: IReviewDao
) : IPoemRoutes {
    override suspend fun postPoem(call: ApplicationCall) {
        val userId = call.getUserId()

        val insertPoem = call.receiveNullable<InsertPoem>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        //TODO: It would be better to have 2 separate objects, one Dto that comes in, parsing the object to a proper data class and filling in the writerId
        val newPoem = poemDao.insertPoem(insertPoem.copy(writerId = userId ?: -1))

        if (newPoem != null) {
            call.respond(HttpStatusCode.Created, newPoem)
        } else {
            call.respondText("Not created", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun getAllPoems(call: ApplicationCall) {
        val categoryId = call.request.queryParameters[CATEGORY_ID_KEY]?.toIntOrNull()

        val poems = poemDao.getPoems(categoryId)
        call.respond(HttpStatusCode.OK, poems)
    }

    override suspend fun getPoemById(call: ApplicationCall) {
        val poemId = call.getPoemId {
            return@getPoemId respondText("Missing id", status = HttpStatusCode.BadRequest)
        }

        val poem = poemDao.getPoem(poemId)

        //TODO: maybe get a couple of things in a collection so the app doesn't have to do 3 seperate calls?
        // e.g. { "poem": {}, "ratings" : {}, "ownReview": {}, "reviews": {}} ----> where reviews are limited to 3 or 5 reviews...

        if (poem != null) {
            call.respond(HttpStatusCode.OK, poem)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }


    override suspend fun updatePoemById(call: ApplicationCall) {
        val poemId = call.getPoemId {
            return@getPoemId respondText("Missing id", status = HttpStatusCode.BadRequest)
        }

        // TODO: should return a better error!!
        val userId = call.getUserId() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = poemDao.isUserWriter(poemId, userId)

        if (!isUserWriter && !isUserAdmin) return call.respondText("You don't have the right permissions to update this poem.", status = HttpStatusCode.BadRequest)

        val updatePoem = call.receive<UpdatePoem>()

        val poem = poemDao.updatePoem(poemId, updatePoem)

        if (poem != null) {
            call.respond(HttpStatusCode.OK, poem)
        } else {
            call.respondText("Not created", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun deletePoemById(call: ApplicationCall) {
        val poemId = call.getPoemId {
            return@getPoemId respondText("Missing id", status = HttpStatusCode.BadRequest)
        }

        // TODO: should return a better error!!
        val userId = call.getUserId() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = poemDao.isUserWriter(poemId, userId)

        if (!isUserWriter && !isUserAdmin) return call.respondText("You don't have the right permissions to delete this poem.", status = HttpStatusCode.BadRequest)

        val success = poemDao.deletePoem(poemId)

        if (success) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun getRatingsForPoem(call: ApplicationCall) {
        val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val ratings = reviewDao.calculateRatings(id)

        call.respond(HttpStatusCode.OK, ratings)
    }
}