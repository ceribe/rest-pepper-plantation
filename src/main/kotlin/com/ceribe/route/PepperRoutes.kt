package com.ceribe.route

import com.ceribe.Database
import com.ceribe.models.Pepper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.pepperRouting() {
    route("peppers") {
        get {
            val requestedName = call.request.queryParameters["name"]
            val matchingPeppersList = Database.getFilteredPeppers(requestedName)
            if (matchingPeppersList.isNotEmpty()) {
                call.respond(matchingPeppersList.toString())
            } else {
                call.respond(HttpStatusCode.NotFound, "No peppers found")
            }
        }
        post {
            val createdPepperId = Database.addDummyPepper()
            call.respond(HttpStatusCode.Created, "Pepper created with id: $createdPepperId")
        }
        route("{id}") {
            get {
                val id = call.parameters["id"]!!.toInt()
                val pepper = Database.getPepperById(id)
                if (pepper != null) {
                    call.respond(pepper)
                } else {
                    call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
                }
            }
            //curl -H Content-Type:application/json -X PUT --data {"name":"Reaper","pot":1,"lastWatering":"0"} http://localhost:8080/peppers/1
            put {
                val id = call.parameters["id"]!!.toInt()
                if (Database.doesPepperExist(id)) {
                    val newPepper = call.receive<Pepper>()
                    newPepper.lastWatering = System.currentTimeMillis().toString()
                    if (Database.getPotCountById(newPepper.potId) == 0) {
                        call.respond(HttpStatusCode.BadRequest, "Pot with id: ${newPepper.potId} does not exist")
                    } else {
                        Database.updatePepper(id, newPepper)
                        call.respond(HttpStatusCode.OK, "Pepper updated")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
                }
            }
            delete {
                val id = call.parameters["id"]!!.toInt()
                val wasPepperDeleted = Database.deletePepperById(id)
                if (wasPepperDeleted) {
                    call.respond(HttpStatusCode.OK, "Pepper deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
                }
            }
            post ("waterings") {
                val id = call.parameters["id"]!!.toInt()
                if (Database.doesPepperExist(id)) {
                    if (Database.waterAmount > 1) {
                        Database.waterAmount -= 1
                        Database.getPepperById(id)?.lastWatering = System.currentTimeMillis().toString()
                        call.respond(HttpStatusCode.OK, "Pepper watered")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Not enough water")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
                }
            }

            post ("repottings") {
                val id = call.parameters["id"]!!.toInt()
                val pepper = Database.getPepperById(id)
                if (pepper != null) {
                    val potId = call.receive<String>().toInt()
                    if (Database.getPotCountById(potId) > 0) {
                        val wasPepperRepotted = Database.repotPepper(pepper, potId)
                        if (wasPepperRepotted) {
                            call.respond(HttpStatusCode.OK, "Pepper repotted")
                        } else {
                            call.respond(HttpStatusCode.BadRequest, "Not enough soil")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "No pot found with id: $potId")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
                }
            }
        }
    }
}
