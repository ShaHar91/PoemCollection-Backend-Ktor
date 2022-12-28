package com.poemcollection.routes

//import com.poemcollection.models.orderStorage
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//
//fun Route.listOrdersRoute() {
//    get("/order") {
//        if (orderStorage.isNotEmpty()) {
//            call.respond(orderStorage)
//        }
//    }
//}
//
//fun Route.getOrderRoute() {
//    get("/order/{id?}") {
//        val id = call.parameters["id"] ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)
//
//        val order = orderStorage.find { it.number == id } ?: return@get call.respondText("Not found", status = HttpStatusCode.NotFound)
//
//        call.respond(order)
//    }
//}
//
//fun Route.totalizeOrderRoute() {
//
//    get("/order/{id?}/total") {
//        val id = call.parameters["id"] ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)
//
//        val order = orderStorage.find { it.number == id } ?: return@get call.respondText("Not found", status = HttpStatusCode.NotFound)
//
//        val total = order.contents.sumOf { it.amount * it.price }
//
//        call.respond(total)
//    }
//}
