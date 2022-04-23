package com.ceribe.plugins

import com.ceribe.route.otherRouting
import com.ceribe.route.pepperRouting
import com.ceribe.route.warehouseRouting
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Welcome to pepper plantation management system!")
        }
        pepperRouting()
        warehouseRouting()
        otherRouting()
    }
}
