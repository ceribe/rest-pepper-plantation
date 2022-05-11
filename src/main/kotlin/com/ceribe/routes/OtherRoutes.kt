package com.ceribe.routes

import com.ceribe.Database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.otherRouting() {
    route("/waterings") {
        post {
            val isEnoughWater = Database.canAllPeppersBeWatered()
            if (!isEnoughWater) {
                call.respond(HttpStatusCode.BadRequest, "Not enough water")
                return@post
            }
            Database.waterAllPeppers()
            call.respond(HttpStatusCode.OK, "All peppers watered")
        }
    }
}
