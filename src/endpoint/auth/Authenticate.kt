package com.directus.endpoint.auth

import com.auth0.jwt.exceptions.TokenExpiredException
import com.directus.ResetPassword
import com.directus.domain.model.AuthToken
import com.directus.domain.model.Credentials
import com.directus.domain.model.PasswordResetToken
import com.directus.domain.service.UserService
import com.directus.endpoint.auth.exception.ExpiredTokenException
import com.directus.endpoint.auth.exception.InvalidCredentialsException
import com.directus.endpoint.auth.exception.UserNotFoundException
import com.directus.endpoint.exception.BadRequestException
import com.directus.errorResponse
import com.directus.jwt.DirectusJWT
import com.directus.successResponse
import domain.model.User
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import org.apache.commons.mail.HtmlEmail

@KtorExperimentalLocationsAPI
fun Route.authentication() {
    post("/authenticate") {
        val credentials = call.receive<Credentials>()
        val user = UserService.getUserByEmail(credentials.email)

        when {
            user == null -> throw UserNotFoundException("User not found!")
            !user.authenticate(credentials.password) -> throw InvalidCredentialsException("Wrong Credentials")

            else -> call.successResponse(HttpStatusCode.OK, AuthToken(user))
        }
    }

    post("/refresh") {
        val body = call.receive<Map<String, String>>()

        val oldToken = body["token"] ?: throw BadRequestException("Missing valid token")
        val verifier = DirectusJWT.verifier
        val userId = verifier.verify(oldToken).getClaim("userId").asInt()
        val user = UserService.getUser(userId) ?: throw UserNotFoundException("User not found!")

        call.successResponse(HttpStatusCode.OK, AuthToken(user))
    }

    route("/password") {
        post("/request") {
            val body = call.receive<Map<String, String>>()

            val email = body["email"] ?: throw BadRequestException("Missing email address")
            val user = UserService.getUserByEmail(email) ?: throw UserNotFoundException("User not found!")

            call.successResponse(HttpStatusCode.OK, PasswordResetToken(user))
        }

        get<ResetPassword> { token ->
            val mail = HtmlEmail()
            call.successResponse(HttpStatusCode.OK, token)

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

    exception<TokenExpiredException> {
        throw TokenExpiredException("Token Expired")
    }

    exception<ExpiredTokenException>  { exception ->
        call.errorResponse(exception)
    }
}

/**
 * Adds the authenticated user to the call pipeline
 * The user can be accessed with "call.user"!
 */
val ApplicationCall.user get() = authentication.principal<User>()