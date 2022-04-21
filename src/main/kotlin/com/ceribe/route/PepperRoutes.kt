package com.ceribe.route

import com.ceribe.Database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.pepperRouting() {
    route("peppers") {
        get {
            if (Database.peppers.isNotEmpty()) {
                call.respond(Database.peppers)
            } else {
                call.respondText("No peppers found", status = HttpStatusCode.OK)
            }
        }
        post {

        }
        route("{id}") {

            get {

            }
            put {

            }
            patch {

            }
            delete {

            }

            post ("waterings") {

            }

            post ("repottings") {

            }
        }
    }
}