package com.ceribe.route

import io.ktor.server.routing.*

fun Route.otherRouting() {
    route("/waterings") {
        post {
        }
    }
}
