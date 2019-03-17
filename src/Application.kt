package com.directus

import com.directus.domain.service.UserService
import com.directus.endpoint.auth.authentication
import com.directus.endpoint.auth.failedAuth
import com.directus.endpoint.auth.user
import com.directus.endpoint.root
import com.directus.jwt.DirectusJWT
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
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
        failedAuth()
    }

    install(Authentication) {
        jwt {
            verifier(DirectusJWT.verifier)
            validate {
                it.payload.getClaim("email").asString().let { email ->
                    UserService.getUserByEmail(email)
                }
            }
        }
    }

    install(ContentNegotiation) {
        gson { setPrettyPrinting() }
    }

    install(Routing) {

        val projectName = environment.config.property("directus.projectName").getString()

        root(projectName) {
            route("/auth") {
                authentication()
            }
        }

        authenticate {
            route("/") {
                get {
                    val user = call.user!!
                    call.respondText { user.email }

                }
            }
        }
    }
}

