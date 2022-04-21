package com.ceribe.route

import io.ktor.server.routing.*

fun Route.warehouseRouting() {
    route("warehouse") {
        get {

        }
        route("water") {
            get {

            }
            put {

            }
        }
        route("soil") {
            get {

            }
            put {

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