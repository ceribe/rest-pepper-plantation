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
                val pots = Database.pots.filter { it.count > 0 }
                if (pots.isNotEmpty()) {
                    call.respond(HttpStatusCode.OK, pots.toString())
                } else {
                    call.respond(HttpStatusCode.NotFound, "No unused pots found")
                }
            }
            post {
                val createPotId = Database.addDummyPot()
                call.respond(HttpStatusCode.Created, "Pot created with id: $createPotId")
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