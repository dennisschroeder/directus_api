package com.directus.endpoint.auth

import com.directus.domain.service.UserService
import com.directus.jwt.DirectusJWT
import domain.model.SuccessResponse
import io.ktor.application.call
import io.ktor.auth.Credential
import io.ktor.auth.authenticate
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import javax.lang.model.type.NullType

fun Route.authentication() {
    post("/authenticate") {
        val credentials = call.receive<Credentials>()
        val user = UserService.getUserByEmail(credentials.email)

        when {
            user == null -> throw Exception("User not found!")
            user.authenticate(credentials.password) -> throw Exception("Wrong Credentials")

            else -> call.respond {
                SuccessResponse<Map<String, String>, NullType>(mapOf("token" to DirectusJWT.sign(user.email)))
            }
        }
    }

    authenticate {
        post("/refresh") {
            call.respondText("Nothing")
        }
    }

}

class InvalidCredentialsException(message: String) : RuntimeException(message)
class Credentials(val email: String, val password: String) : Credential