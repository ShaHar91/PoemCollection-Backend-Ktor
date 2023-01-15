package com.poemcollection.routes

import com.poemcollection.data.models.InsertPoem
import com.poemcollection.data.models.UpdatePoem
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.routes.ParamConstants.CATEGORY_ID_KEY
import com.poemcollection.routes.ParamConstants.POEM_ID_KEY
import com.poemcollection.routes.interfaces.IPoemRoutes
import com.poemcollection.security.security.token.TokenClaim
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class PoemRoutesImpl(
    private val poemDao: IPoemDao,
    private val reviewDao: IReviewDao
) : IPoemRoutes {
    override suspend fun postPoem(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)

        val insertPoem = call.receiveNullable<InsertPoem>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        // TODO: `writerId` should be taken out of the `insertPoem` class since this can be a security risk!!!
        val newPoem = poemDao.insertPoem(insertPoem.copy(writerId = userId?.toIntOrNull() ?: -1))

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
        val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val poem = poemDao.getPoem(id)

        //TODO: maybe get a couple of things in a collection so the app doesn't have to do 3 seperate calls?
        // e.g. { "poem": {}, "ratings" : {}, "ownReview": {}, "reviews": {}} ----> where reviews are limited to 3 or 5 reviews...

        if (poem != null) {
            call.respond(HttpStatusCode.OK, poem)
        } else {
            call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun updatePoemById(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)

        // TODO: should only be able to update when the userId (principal) is the same as the authenticated user!! (Or is an admin)
        val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val updatePoem = call.receive<UpdatePoem>()

        val poem = poemDao.updatePoem(id, updatePoem)

        if (poem != null) {
            call.respond(HttpStatusCode.OK, poem)
        } else {
            call.respondText("Not created", status = HttpStatusCode.InternalServerError)
        }
    }

    override suspend fun deletePoemById(call: ApplicationCall) {
        // TODO: should only be able to delete when the writerId is the same as the authenticated user!! (Or is an admin)
        val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return call.respondText("Missing id", status = HttpStatusCode.BadRequest)

        val success = poemDao.deletePoem(id)

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