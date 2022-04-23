package com.ceribe.route

import com.ceribe.Database
import com.ceribe.models.Pot
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.warehouseRouting() {
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
            Database.waterAmount = body.toInt()
            call.respond(HttpStatusCode.OK, "Water: ${Database.waterAmount}")
        }
    }

    route("warehouse/soil") {
        get {
            call.respond(HttpStatusCode.OK, "${Database.soilAmount}")
        }
        put {
            val body = call.receive<String>()
            Database.soilAmount = body.toInt()
            call.respond(HttpStatusCode.OK, "Soil: ${Database.soilAmount}")
        }
    }

    route ("warehouse/pots") {
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
    }

    route("warehouse/pots/{id}") {
        get {
//            val etag = call.request.headers["ETag"]
            val id = call.parameters["id"]!!.toInt()
            val pepper = Database.getPot(id)
            if (pepper != null) {
//                call.response.etag("sfsdf")
                call.respond(HttpStatusCode.OK, pepper)
            } else {
                call.respond(HttpStatusCode.NotFound, "No pepper found with id: $id")
            }
        }
        //curl -H Content-Type:application/json -X PUT http://localhost:8080/warehouse/pots/1 --data {"name":"small","count":10}
        put {
            val id = call.parameters["id"]!!.toInt()
            if (Database.doesPotExist(id)) {
                val newPot = call.receive<Pot>()
                Database.updatePot(id, newPot)
                call.respond(HttpStatusCode.OK, "Pot updated")
            } else {
                call.respond(HttpStatusCode.NotFound, "No pot found with id: $id")
            }
        }
        delete {
            val id = call.parameters["id"]!!.toInt()
            val wasPotDeleted = Database.deletePot(id)
            if (wasPotDeleted) {
                call.respond(HttpStatusCode.OK, "Pot deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "No pot found with id: $id")
            }
        }
    }
}