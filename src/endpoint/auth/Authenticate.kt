package com.directus.endpoint.auth

import com.directus.domain.model.Credentials
import com.directus.domain.model.JwtToken
import com.directus.domain.service.UserService
import com.directus.endpoint.auth.exception.InvalidCredentialsException
import com.directus.endpoint.auth.exception.UserNotFoundException
import com.directus.errorResponse
import com.directus.successResponse
import domain.model.User
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respondText
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

    authenticate {
        post("/refresh") {
            call.respondText("Nothing")
        }
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

val ApplicationCall.user get() = authentication.principal<User>()