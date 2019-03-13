package com.directus.endpoint.auth

import com.directus.jwt.DirectusJWT
import domain.model.SuccessResponse
import domain.model.User
import domain.model.Users
import io.ktor.application.call
import io.ktor.auth.Credential
import io.ktor.auth.authenticate
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import org.jetbrains.exposed.sql.transactions.transaction
import javax.lang.model.type.NullType

fun Route.authentication() {
    post("/authenticate") {
        val post = call.receive<Credentials>()
        val user = transaction {
             User.find {
                Users.email eq post.email
            }.singleOrNull() ?: throw Exception("Fuck this shit")

        }
        val response =
            SuccessResponse<Map<String, String>, NullType>(mapOf("token" to DirectusJWT.sign(user.email)))
        call.respond(response)
    }

    authenticate {
        post("/refresh") {
            call.respondText("Nothing")
        }
    }

}

class InvalidCredentialsException(message: String) : RuntimeException(message)
class Credentials(val email: String, val password: String): Credential