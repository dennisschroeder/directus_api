package com.directus

import com.directus.endpoint.auth.InvalidCredentialsException
import com.directus.endpoint.auth.authentication
import com.directus.endpoint.base
import com.directus.jwt.DirectusJWT
import domain.model.ErrorResponse
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.route
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.main(testing: Boolean = false) {

    install(Locations)
    install(DefaultHeaders)

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost() // @ToDo: Remove in Production
    }

    install(StatusPages) {
        exception<InvalidCredentialsException> { exception ->
            val message = ErrorResponse(
                HttpStatusCode.Unauthorized.value,
                exception.message ?: HttpStatusCode.Unauthorized.description
            )
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to message))
        }
    }

    install(Authentication) {
        jwt {
            verifier(DirectusJWT.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").toString())
            }
        }
    }

    install(ContentNegotiation) {
        gson { setPrettyPrinting() }
    }

    install(Routing) {

        val projectName = environment.config.propertyOrNull("directus.projectName")?.getString() ?: "_"

        base(projectName) {
            route("/auth") {
                authentication()
            }
        }

        authenticate {
            route("/", HttpMethod.Get) {
                handle {

                    call.respondText("Success")
                }
            }
        }
    }
}

