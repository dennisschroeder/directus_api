package endpoints

import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.util.pipeline.ContextDsl

@ContextDsl
fun Route.project(build: Route.() -> Unit) {

    // The base route can be set in application.config file
    // defaults to: _ (underscore)
    route("{projectKey}") {

    }.apply(build)
}