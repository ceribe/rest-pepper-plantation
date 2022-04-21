package com.ceribe.route

import com.ceribe.Database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.warehouseRouting() {
    route("warehouse") {
        get {
            call.respond(HttpStatusCode.OK, "Water: ${Database.waterAmount}\nSoil: ${Database.soilAmount}\nPots: ${Database.pots}")
        }
        route("water") {
            get {
                call.respond(HttpStatusCode.OK, "${Database.waterAmount}")
            }

            put {
                val body = call.receive<String>()
                Database.waterAmount = body.toInt()
                call.respond(HttpStatusCode.OK, "Water: ${Database.waterAmount}")
            }
        }
        route("soil") {
            get {
                call.respond(HttpStatusCode.OK, "${Database.soilAmount}")
            }
            put {
                val body = call.receive<String>()
                Database.soilAmount = body.toInt()
                call.respond(HttpStatusCode.OK, "Soil: ${Database.soilAmount}")
            }
        }
        route ("pots") {
            get {

            }

            post {

            }
            route("{id}") {
                get {

                }
                put {

                }
                delete {

                }
            }
        }
    }
}