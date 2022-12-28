package com.poemcollection.plugins

//import com.poemcollection.routes.customerRouting
//import com.poemcollection.routes.getOrderRoute
//import com.poemcollection.routes.listOrdersRoute
//import com.poemcollection.routes.totalizeOrderRoute
import com.poemcollection.routes.userRouting
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureRouting() {

    routing {

        userRouting()
//        customerRouting()
//        listOrdersRoute()
//        getOrderRoute()
//        totalizeOrderRoute()
    }
}
