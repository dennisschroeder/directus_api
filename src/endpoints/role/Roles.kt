package com.directus.endpoints.roles

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route

@KtorExperimentalLocationsAPI
fun Route.roles() {

    route("/roles") {
        post {

        }
    }
}