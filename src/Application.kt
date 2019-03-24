package com.directus

import com.directus.auth.AuthService
import com.directus.auth.jwt
import com.directus.domain.service.UserService
import com.directus.endpoint.auth.authentication
import com.directus.endpoint.auth.failedAuth
import com.directus.endpoint.root
import com.directus.repository.database.DatabaseService
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            validate { credentials ->
                when {
                    credentials.payload.getClaim("type").asString() != "auth" -> null
                    credentials.payload.getClaim("key").asString() != ConfigService.configs[credentials.projectKey]?.auth!!.privateKey -> null
                    else -> credentials.payload.getClaim("userId").asInt().let { userId ->
                        UserService.getUser(userId)
                    }
                }
            }

            verifier { _, projectKey ->
                AuthService.verifier(projectKey)
            }
        }
    }

    install(ContentNegotiation) {
        gson { setPrettyPrinting() }
    }

    install(Routing) {
        get("/") {
            call.respondText { "Serving application information soon..." }
        }

        get("/server/ping") {
            call.respondText { "pong" }
        }

        root("{projectKey}") {

            route("/auth") {
                authentication()
            }
        }
    }
}

fun <Q>ApplicationCall.query(query: () -> Q) {
   org.jetbrains.exposed.sql.transactions.transaction(dbConnection) {query()}
}

suspend fun <Q>ApplicationCall.asyncQuery(query: () -> Q) = withContext(Dispatchers.IO) {
    org.jetbrains.exposed.sql.transactions.transaction(dbConnection) { query() }
}

val ApplicationCall.projectKey get() = parameters["projectKey"]
val ApplicationCall.dbConnection get() =  DatabaseService.connections[projectKey]