package com.ceribe.route

import com.ceribe.Database
import com.ceribe.models.Pepper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.pepperRouting() {

    suspend fun checkETag(call: ApplicationCall, pepperId: Int): Boolean {
        val etag = call.request.header("ETag")
        if (etag == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing ETag header")
            return false
        }
        if (etag != Database.getPeppersETag(pepperId)) {
            call.respond(HttpStatusCode.PreconditionFailed, "ETag does not match")
            return false
        }
        return true
    }

    route("peppers") {
        get {
            val requestedName = call.request.queryParameters["name"]
            val matchingPeppersList = Database.getFilteredPeppers(requestedName)
            if (matchingPeppersList.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, matchingPeppersList.toString())
            } else {
                call.respond(HttpStatusCode.NotFound, "No peppers found")
            }
        }
        post {
            val createdPepperId = Database.addDummyPepper()
            call.response.etag(Database.getPeppersETag(createdPepperId))
            call.respond(HttpStatusCode.Created, "Pepper created with id: $createdPepperId")
        }
    }

    route("peppers/{id}") {
        get {
            val id = call.parameters["id"]!!.toInt()
            val pepper = Database.getPepper(id)
            if (pepper != null) {
                call.response.etag(Database.getPeppersETag(id))
                call.respond(pepper)
            } else {
                call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
            }
        }
        //curl -H Content-Type:application/json -X PUT --data {"name":"Reaper","pot":1,"lastWatering":"0"} http://localhost:8080/peppers/1
        put {
            val id = call.parameters["id"]!!.toInt()
            val etagMatches = checkETag(call, id)
            if (!etagMatches) return@put
            if (Database.doesPepperExist(id)) {
                val newPepper = call.receive<Pepper>()
                newPepper.lastWatering = System.currentTimeMillis()
                if (Database.getPotCount(newPepper.potId) == 0) {
                    call.respond(HttpStatusCode.BadRequest, "Pot with id: ${newPepper.potId} does not exist")
                } else {
                    Database.updatePepper(id, newPepper)
                    call.response.etag(Database.getPeppersETag(id))
                    call.respond(HttpStatusCode.OK, "Pepper updated")
                }
            } else {
                call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
            }
        }
        delete {
            val id = call.parameters["id"]!!.toInt()
            val wasPepperDeleted = Database.deletePepper(id)
            if (wasPepperDeleted) {
                call.respond(HttpStatusCode.OK, "Pepper deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
            }
        }
    }

    route ("peppers/{id}/waterings") {
        post {
            val id = call.parameters["id"]!!.toInt()
            val etagMatches = checkETag(call, id)
            if (!etagMatches) return@post
            if (Database.doesPepperExist(id)) {
                if (Database.waterAmount > 1) {
                    Database.waterAmount -= 1
                    Database.waterPepper(id)
                    call.response.etag(Database.getPeppersETag(id))
                    call.respond(HttpStatusCode.OK, "Pepper watered")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Not enough water")
                }
            } else {
                call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
            }
        }
    }

    route ("peppers/{id}/repottings") {
        post {
            val pepperId = call.parameters["id"]!!.toInt()
            val etagMatches = checkETag(call, pepperId)
            if (!etagMatches) return@post
            val pepper = Database.getPepper(pepperId)
            if (pepper != null) {
                val potId = call.receive<String>().toInt()
                if (Database.getPotCount(potId) > 0) {
                    val wasPepperRepotted = Database.repotPepper(pepperId, potId)
                    if (wasPepperRepotted) {
                        call.response.etag(Database.getPeppersETag(pepperId))
                        call.respond(HttpStatusCode.OK, "Pepper repotted")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Not enough soil")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "No pot found with id: $potId")
                }
            } else {
                call.respond(HttpStatusCode.NotFound, "No pepper found with id: $pepperId")
            }
        }
    }
}