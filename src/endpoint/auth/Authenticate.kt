package com.directus.endpoint.auth

import com.auth0.jwt.exceptions.TokenExpiredException
import com.directus.*
import com.directus.auth.AuthService
import com.directus.auth.AuthToken
import com.directus.auth.PasswordResetToken
import com.directus.auth.exception.NoProjectKeyException
import com.directus.config.exception.ApiConfigurationNotFoundException
import com.directus.domain.model.Credentials
import com.directus.domain.model.User
import com.directus.domain.service.UserService
import com.directus.domain.service.UtilService
import com.directus.endpoint.auth.exception.ExpiredTokenException
import com.directus.endpoint.auth.exception.InvalidCredentialsException
import com.directus.endpoint.auth.exception.UserNotFoundException
import com.directus.endpoint.exception.BadRequestException
import com.directus.mail.MailService
import com.directus.repository.database.asyncTransaction
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
import org.mindrot.jbcrypt.BCrypt

@KtorExperimentalLocationsAPI
fun Route.authentication() {
    post("/authenticate") {
        val credentials = call.receive<Credentials>()
        val projectKey = call.projectKey
        val user = call.asyncTransaction { UserService.getUserByEmail(credentials.email) }

        when {
            user == null -> throw UserNotFoundException("User not found!")
            !user.authenticate(credentials.password) -> throw InvalidCredentialsException("Wrong Credentials")

            else -> call.successResponse(HttpStatusCode.OK, AuthToken(AuthService.signAuthToken(user, projectKey)))
        }
    }

    post("/refresh") {
        val body = call.receive<Map<String, String>>()
        val oldToken = body["token"] ?: throw BadRequestException("Missing valid token")

        val projectKey = call.projectKey
        val verifier = AuthService.verifier(projectKey)
        val userId = verifier.verify(oldToken).getClaim("userId").asInt()

        val user = call.asyncTransaction {
            UserService.getUser(userId) ?: throw UserNotFoundException("User not found!")
        }

        val token = AuthService.signAuthToken(user, projectKey)

        call.successResponse(HttpStatusCode.OK, AuthToken(token))
    }

    route("/password") {
        post("/request") {
            val body = call.receive<Map<String, String>>()
            val email = body["email"] ?: throw BadRequestException("Missing email address")
            val user = call.asyncTransaction {
                UserService.getUserByEmail(email) ?: throw UserNotFoundException("User not found!")
            }

            val projectKey = call.projectKey
            val token = AuthService.signPasswordRequestToken(user, projectKey)

            call.successResponse(HttpStatusCode.OK, PasswordResetToken(token))
        }

        get<ResetPassword> { token ->
            val projectKey = call.projectKey
            val verifier = AuthService.verifier(projectKey)
            val emailFromToken = verifier.verify(token.resetToken).getClaim("email").asString()

            val user = call.asyncTransaction {
                UserService.getUserByEmail(emailFromToken) ?: throw UserNotFoundException("User not found!")
            }

            val password = UtilService.genRandomString(32)

            val message = MailService.createMessage {
                from(ConfigService.configs[projectKey]!!.mail.from)
                withSubject("Your new password!")
                withPlainText("Hello <b>${user.firstName}</b>! Here is your new password: $password")
                to(user.email)
            }

            MailService.createTransporter(projectKey).sendMail(message)

            call.asyncTransaction {
                user.password = BCrypt.hashpw(password, BCrypt.gensalt())
            }

            call.successResponse(HttpStatusCode.OK, token)
        }
    }
}

fun StatusPages.Configuration.failedAuth() {
    exception<InvalidCredentialsException> { call.errorResponse(it) }

    exception<UserNotFoundException> { call.errorResponse(it) }

    exception<TokenExpiredException> { throw ExpiredTokenException(it.message!!) }
    exception<ExpiredTokenException>  { call.errorResponse(it) }

    exception<NoProjectKeyException>  { call.errorResponse(it) }

    exception<ApiConfigurationNotFoundException>  { call.errorResponse(it) }
}

/**
 * Adds the authenticated user to the call pipeline
 * The user can be accessed with "call.user"!
 */
val ApplicationCall.user get() = authentication.principal<User>()