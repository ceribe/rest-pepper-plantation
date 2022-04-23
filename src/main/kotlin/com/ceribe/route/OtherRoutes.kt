package com.ceribe.route

import com.ceribe.Database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.otherRouting() {
    route("/waterings") {
        post {
            val pepperCount = Database.peppers.size
            val isEnoughWater = pepperCount <= Database.waterAmount
            if (isEnoughWater) {
                Database.waterAmount -= pepperCount
                Database.peppers.forEach {
                    it.lastWatering = System.currentTimeMillis()
                }
                Database.updateAllPeppersETag()
                call.respond(HttpStatusCode.OK, "All peppers watered")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Not enough water")
            }
        }
    }
}
