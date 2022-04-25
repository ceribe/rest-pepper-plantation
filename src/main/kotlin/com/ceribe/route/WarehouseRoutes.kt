package com.ceribe.route

import com.ceribe.Database
import com.ceribe.models.Pot
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.warehouseRouting() {

    suspend fun checkETag(call: ApplicationCall, potId: Int): Boolean {
        val etag = call.request.header("ETag")
        if (etag == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing ETag header")
            return false
        }
        if (etag != Database.getPotsETag(potId)) {
            call.respond(HttpStatusCode.PreconditionFailed, "ETag does not match")
            return false
        }
        return true
    }

    route("warehouse") {
        get {
            call.respond(
                HttpStatusCode.OK,
                "Water: ${Database.waterAmount}\nSoil: ${Database.soilAmount}\nPots: ${Database.pots}"
            )
        }
    }

    route("warehouse/water") {
        get {
            call.respond(HttpStatusCode.OK, "${Database.waterAmount}")
        }

        put {
            val body = call.receive<String>()
            try {
                Database.waterAmount = body.toInt()
                call.respond(HttpStatusCode.OK, "Water: ${Database.waterAmount}")
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid water amount")
            }
        }
    }

    route("warehouse/soil") {
        get {
            call.respond(HttpStatusCode.OK, "${Database.soilAmount}")
        }
        put {
            val body = call.receive<String>()
            try {
                Database.soilAmount = body.toInt()
                call.respond(HttpStatusCode.OK, "Soil: ${Database.soilAmount}")
            } catch (e: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid soil amount")
            }
        }
    }

    route ("warehouse/pots") {
        get {
            val pots = Database.pots.filter { it.count > 0 }
            if (pots.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, pots)
            } else {
                call.respond(HttpStatusCode.NotFound, "No unused pots found")
            }
        }
        post {
            val createdPotId = Database.addDummyPot()
            with(call) {
                response.etag(Database.getPotsETag(createdPotId))
                respond(HttpStatusCode.Created, "Pot created with id: $createdPotId")
            }
        }
    }

    route("warehouse/pots/{id}") {
        get {
            val id = call.parameters["id"]!!.toInt()
            val pot = Database.getPot(id)
            if (pot == null) {
                call.respond(HttpStatusCode.NotFound, "No pot found with id: $id")
                return@get
            }
            with(call) {
                response.etag(Database.getPotsETag(id))
                respond(HttpStatusCode.OK, pot)
            }
        }
        //curl -H Content-Type:application/json -X PUT http://localhost:8080/warehouse/pots/1 --data {"name":"small","count":10}
        put {
            val id = call.parameters["id"]!!.toInt()
            val etagMatches = checkETag(call, id)
            if (!etagMatches) return@put
            if (!Database.doesPotExist(id)) {
                call.respond(HttpStatusCode.NotFound, "No pot found with id: $id")
                return@put
            }
            val updatedPot = call.receive<Pot>()
            Database.updatePot(id, updatedPot)
            with(call) {
                response.etag(Database.getPotsETag(id))
                respond(HttpStatusCode.OK, "Pot updated")
            }
        }
        delete {
            val id = call.parameters["id"]!!.toInt()
            val anyPepperUsesThisPot = Database.peppers.any { it.potId == id }
            if (anyPepperUsesThisPot) {
                call.respond(HttpStatusCode.BadRequest, "Some pepper is using this type of pot")
                return@delete
            }
            val wasPotDeleted = Database.deletePot(id)
            if (wasPotDeleted) {
                call.respond(HttpStatusCode.OK, "Pot deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "No pot found with id: $id")
            }
        }
    }
}