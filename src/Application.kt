package com.directus

import com.auth0.jwt.exceptions.TokenExpiredException
import com.directus.auth.AuthService
import com.directus.auth.exception.NoProjectKeyException
import com.directus.auth.jwt
import com.directus.config.ConfigService
import com.directus.config.exception.ApiConfigurationNotFoundException
import com.directus.domain.service.UserService
import com.directus.endpoints.auth.authentication
import com.directus.endpoints.auth.exception.ExpiredTokenException
import com.directus.endpoints.role.roles
import com.directus.endpoints.users.users
import com.directus.repository.database.DatabaseService
import endpoints.project
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
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
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
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
        exception<TokenExpiredException> { throw ExpiredTokenException(it.message!!) }
        exception<AbstractBaseException> { call.errorResponse(it) }
    }

    install(Authentication) {
        jwt {
            validate { credentials ->
                when {
                    credentials.payload.getClaim("type").asString() != "auth" -> null
                    credentials.payload.getClaim("key").asString() !=
                            ConfigService.configs[credentials.projectKey]?.auth!!.privateKey -> null
                    else -> credentials.payload.getClaim("userId").asInt().let { userId ->
                        DatabaseService.asyncTransaction(credentials.projectKey) {
                            UserService.getActiveUser(userId)
                        }
                    }
                }
            }

            verifier { _, projectKey ->
                AuthService.verifier(projectKey)
            }
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        get("/") {
            call.respondText { "Serving application information soon..." }
        }

        get("/server/ping") {
            call.respondText { "pong" }
        }

        project {
            route("/auth") {
                authentication()
            }

            authenticate {
                users()
                roles()
            }
        }
    }
}

val ApplicationCall.projectKey
    get() = when {
        parameters["projectKey"] == null ->
            throw NoProjectKeyException("No project key found in private route")
        !ConfigService.configs.containsKey(parameters["projectKey"]) ->
            throw ApiConfigurationNotFoundException("No project found that matches project configuration")
        else -> parameters["projectKey"]!!
    }