package com.ceribe.routes

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
            if (matchingPeppersList.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "No peppers found")
                return@get
            }
            val start = call.request.queryParameters["start"]?.toIntOrNull() ?: 0
            if (start < 0) {
                call.respond(HttpStatusCode.BadRequest, "Invalid page")
                return@get
            }
            var limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 5
            limit = limit.coerceAtMost(10)
            limit = limit.coerceAtLeast(1)
            val limitedPeppers = matchingPeppersList.drop(start).take(limit)
            if (limitedPeppers.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "No peppers found")
                return@get
            }
            call.response.header("Total-Count", matchingPeppersList.size)
            if (start + limit < matchingPeppersList.size) {
                call.response.header("Next-Page", "/peppers?start=${start + limit}&limit=$limit" +
                        if (requestedName != null) "&name=$requestedName" else "")
            }
            if (start - limit >= 0) {
                call.response.header("Previous-Page", "/peppers?start=${start - limit}&limit=$limit" +
                        if (requestedName != null) "&name=$requestedName" else "")
            }
            call.respond(HttpStatusCode.OK, limitedPeppers)
        }
        post {
            val createdPepperId = Database.addDummyPepper()
            with(call) {
                response.etag(Database.getPeppersETag(createdPepperId))
                respond(HttpStatusCode.Created, "Pepper created with id: $createdPepperId")
            }
        }
    }

    route("peppers/{id}") {
        get {
            val id = call.parameters["id"]!!.toInt()
            val pepper = Database.getPepper(id)
            if (pepper == null) {
                call.respond(HttpStatusCode.NotFound, "Pepper with id $id not found")
                return@get
            }
            with(call) {
                response.etag(Database.getPeppersETag(id))
                respond(HttpStatusCode.OK, pepper)
            }
        }
        //curl -H Content-Type:application/json -H Etag:??? -X PUT --data {"name":"Reaper","potId":1,"lastWatering":"0"} http://localhost:8080/peppers/1
        put {
            val id = call.parameters["id"]!!.toInt()
            val etagMatches = checkETag(call, id)
            if (!etagMatches) return@put
            if (!Database.doesPepperExist(id)) {
                call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
                return@put
            }
            val updatedPepper = call.receiveOrNull<Pepper>()
            if (updatedPepper == null) {
                call.respond(HttpStatusCode.BadRequest, "No pepper received")
                return@put
            }
            if (updatedPepper.lastWatering == 0L) {
                updatedPepper.lastWatering = Database.getPepper(id)?.lastWatering ?: 0
            }
            if (Database.getPotCount(updatedPepper.potId) == 0) {
                call.respond(HttpStatusCode.BadRequest, "Pot with id: ${updatedPepper.potId} does not exist")
                return@put
            }
            Database.updatePepper(id, updatedPepper)
            with(call) {
                response.etag(Database.getPeppersETag(id))
                respond(HttpStatusCode.OK, "Pepper updated")
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
            if (!Database.doesPepperExist(id)) {
                call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
                return@post
            }
            if (Database.waterAmount == 0) {
                call.respond(HttpStatusCode.BadRequest, "Not enough water")
                return@post
            }
            Database.apply {
                waterAmount -= 1
                waterPepper(id)
            }
            with(call) {
                response.etag(Database.getPeppersETag(id))
                respond(HttpStatusCode.OK, "Pepper watered")
            }
        }
    }

    route ("peppers/{id}/repottings") {
        post {
            val pepperId = call.parameters["id"]!!.toInt()
            val etagMatches = checkETag(call, pepperId)
            if (!etagMatches) return@post
            val pepper = Database.getPepper(pepperId)
            if (pepper == null) {
                call.respond(HttpStatusCode.NotFound, "No pepper found with id: $pepperId")
                return@post
            }
            val potId = call.receiveOrNull<String>()?.toInt()
            if (potId == null) {
                call.respond(HttpStatusCode.BadRequest, "No pot id received")
                return@post
            }
            if (Database.getPotCount(potId) == 0) {
                call.respond(HttpStatusCode.BadRequest, "No pot found with id: $potId")
                return@post
            }
            val wasPepperRepotted = Database.repotPepper(pepperId, potId)
            if (!wasPepperRepotted) {
                call.respond(HttpStatusCode.BadRequest, "Not enough soil")
                return@post
            }
            with(call) {
                response.etag(Database.getPeppersETag(pepperId))
                respond(HttpStatusCode.OK, "Pepper repotted")
            }
        }
    }
}
