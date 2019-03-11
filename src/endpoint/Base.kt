package com.directus.endpoint

import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.util.pipeline.ContextDsl

@ContextDsl
fun Route.base(projectName: String, build: Route.() -> Unit) {

    // The base route can be set in application.config file
    // defaults to: _ (underscore)
    route("/$projectName") {

    }.apply(build)
}
