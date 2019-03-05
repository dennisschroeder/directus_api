package com.directus

import com.directus.endpoint.auth.InvalidCredentialsException
import com.directus.endpoint.auth.authenticate
import com.directus.jwt.DirectusJWT
import com.directus.model.*
import com.directus.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.features.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.gson.*
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    if (!testing)println("Application Started")

    DatabaseFactory.init()
    val tables = listOf<Table>(DirectusUsers, DirectusSettings)
    DatabaseFactory.createTables(tables = tables)

    transaction {
        DirectusSettings.insert {
            it[key] = "project_name"
            it[value] = "Directus"
        }
    }

    install(Locations)
    install(DefaultHeaders)
    install(CallLogging)

    install(CORS) {
        method(HttpMethod.Put)
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
            val message = ErrorResponse(HttpStatusCode.Unauthorized.value, exception.message ?: HttpStatusCode.Unauthorized.description)
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
        gson {
            setPrettyPrinting()
        }
    }

    install(Routing) {

        val projectName = environment.config.propertyOrNull("directus.projectName")?.getString()

        // The base route can be set in application.config file
        // defaults to: _ (underscore)
        route("/$projectName") {
            route("/auth") {
                authenticate()
            }
        }
    }

    routing {
        get("/") {

            call.respondText("HELLO DENNIS!", contentType = ContentType.Text.Plain)
        }
    }
}

