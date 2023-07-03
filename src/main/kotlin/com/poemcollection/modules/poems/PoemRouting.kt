package com.poemcollection.modules.poems

import com.poemcollection.ParamConstants
import com.poemcollection.data.dto.requests.poem.InsertOrUpdatePoem
import com.poemcollection.utils.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.poemRouting() {

    val poemController by inject<PoemController>()

    route("poems") {
        authenticate {
            // Only a real user can create a poem
            post {
                val insertPoem = call.receiveOrRespondWithError<InsertOrUpdatePoem>()
                val userId = call.authenticatedUser.id
                val poem = poemController.postPoem(userId, insertPoem)
                call.respond(poem)
            }
        }

        get {
            val categoryId = call.getCategoryIdNullable()
            val poems = poemController.getAllPoems(categoryId)
            call.respond(poems)
        }

        route("{${ParamConstants.POEM_ID_KEY}}") {

            get {
                val poemId = call.getPoemId()
                val poemDetail = poemController.getPoemById(poemId)
                call.respond(poemDetail)
            }

            authenticate {
                put {
                    val poemId = call.getPoemId()
                    val updatePoem = call.receiveOrRespondWithError<InsertOrUpdatePoem>()
                    val userId = call.authenticatedUser.id
                    val poemDetail = poemController.updatePoemById(userId, poemId, updatePoem)
                    call.respond(poemDetail)
                }

                delete {
                    val poemId = call.getPoemId()
                    val userId = call.authenticatedUser.id
                    poemController.deletePoemById(userId, poemId)
                    sendOk()
                }
            }

            get("ratings") {
                val poemId = call.getPoemId()
                val ratings = poemController.getRatingsForPoem(poemId)
                call.respond(ratings)
            }
        }
    }
}