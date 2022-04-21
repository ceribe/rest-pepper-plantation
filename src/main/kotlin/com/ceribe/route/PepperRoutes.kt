package com.ceribe.route

import com.ceribe.Database
import com.ceribe.models.Pepper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.pepperRouting() {
    route("peppers") {
        get {
            if (Database.peppers.isNotEmpty()) {
                call.respond(Database.peppers.toString())
            } else {
                call.respond(HttpStatusCode.NotFound, "No peppers found")
            }
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

            post ("waterings") {

            }

            post ("repottings") {

            }
        }
    }
}