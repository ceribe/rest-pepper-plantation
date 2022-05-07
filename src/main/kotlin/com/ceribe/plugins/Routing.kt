package com.ceribe.plugins

import com.ceribe.routes.otherRouting
import com.ceribe.routes.pepperRouting
import com.ceribe.routes.warehouseRouting
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
