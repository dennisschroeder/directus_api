package com.directus.endpoint.auth

import com.directus.domain.model.Credentials
import com.directus.domain.model.JwtToken
import com.directus.domain.service.UserService
import com.directus.endpoint.auth.exception.InvalidCredentialsException
import com.directus.endpoint.auth.exception.UserNotFoundException
import com.directus.errorResponse
import com.directus.jwt.DirectusJWT
import com.directus.successResponse
import domain.model.User
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.post

fun Route.authentication() {
    post("/authenticate") {
        val credentials = call.receive<Credentials>()
        val user = UserService.getUserByEmail(credentials.email)

        when {
            user == null -> throw UserNotFoundException("User not found!")
            !user.authenticate(credentials.password) -> throw InvalidCredentialsException("Wrong Credentials")

            else -> call.successResponse(HttpStatusCode.OK, JwtToken(user.email))
        }
    }

    post("/refresh") {
        val body = call.receive<Map<String, String>>()

        if (body["token"] == null) {
            throw com.directus.endpoint.exception.BadRequestException("Missing valid token")
        }

        val verifier = DirectusJWT.verifier
        val email = verifier.verify(body["token"]).getClaim("email").asString()

        call.successResponse(HttpStatusCode.OK, JwtToken(email))
    }

}

fun StatusPages.Configuration.failedAuth() {
    exception<InvalidCredentialsException> { exception ->
        call.errorResponse(exception)
    }

    exception<UserNotFoundException> { exception ->
        call.errorResponse(exception)
    }
}

/**
 * Adds the authenticated user to the call pipeline
 * The user can be accessed with "call.user"!
 */
val ApplicationCall.user get() = authentication.principal<User>()