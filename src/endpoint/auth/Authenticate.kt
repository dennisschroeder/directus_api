package com.directus.endpoint.auth

import com.auth0.jwt.exceptions.TokenExpiredException
import com.directus.ResetPassword
import com.directus.auth.AuthService
import com.directus.auth.AuthToken
import com.directus.auth.PasswordResetToken
import com.directus.domain.model.Credentials
import com.directus.domain.service.UserService
import com.directus.endpoint.auth.exception.ExpiredTokenException
import com.directus.endpoint.auth.exception.InvalidCredentialsException
import com.directus.endpoint.auth.exception.UserNotFoundException
import com.directus.endpoint.exception.BadRequestException
import com.directus.errorResponse
import com.directus.mail.MailService
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

@KtorExperimentalLocationsAPI
fun Route.authentication() {
    post("/authenticate") {
        val credentials = call.receive<Credentials>()
        val projectKey = call.parameters["projectKey"]!!
        val user = UserService.getUserByEmail(credentials.email)

        when {
            user == null -> throw UserNotFoundException("User not found!")
            !user.authenticate(credentials.password) -> throw InvalidCredentialsException("Wrong Credentials")

            else -> call.successResponse(HttpStatusCode.OK, AuthToken(user, projectKey))
        }
    }

    post("/refresh") {
        val body = call.receive<Map<String, String>>()
        val projectKey = call.parameters["projectKey"]!!
        val oldToken = body["token"] ?: throw BadRequestException("Missing valid token")
        val verifier = AuthService.verifier(projectKey)
        val userId = verifier.verify(oldToken).getClaim("userId").asInt()
        val user = UserService.getUser(userId) ?: throw UserNotFoundException("User not found!")

        call.successResponse(HttpStatusCode.OK, AuthToken(user, projectKey))
    }

    route("/password") {
        post("/request") {
            val body = call.receive<Map<String, String>>()

            val email = body["email"] ?: throw BadRequestException("Missing email address")
            val user = UserService.getUserByEmail(email) ?: throw UserNotFoundException("User not found!")

            call.successResponse(HttpStatusCode.OK, PasswordResetToken(user))
        }

        get<ResetPassword> { token ->
            val transporter = MailService.createTransporter()

            val message = MailService.createMessage().apply {
                withSubject("From Directus")
                withPlainText("Hello World")
                to("dennis@example.com")
            }.buildEmail()

            transporter.sendMail(message, true)
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

    exception<TokenExpiredException> {exception ->
        throw ExpiredTokenException(exception.message!!)
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