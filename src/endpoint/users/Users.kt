package com.directus.endpoint.users

import com.directus.UserId
import com.directus.UserIds
import com.directus.domain.model.UserReceiver
import com.directus.domain.service.UserService
import com.directus.endpoint.auth.exception.UserNotFoundException
import com.directus.endpoint.auth.user
import com.directus.repository.database.asyncTransaction
import com.directus.successResponse
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.locations.patch
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.mindrot.jbcrypt.BCrypt

@KtorExperimentalLocationsAPI
fun Route.users() {

    route("/users") {
        post {
            val user = call.receive<UserReceiver>()

            call.asyncTransaction {
                UserService.createUser {
                    email = user.email
                    password = user.password
                    status = user.status
                    firstName = user.firstName
                    lastName = user.lastName
                    email = user.email
                    password = user.password
                    token = user.token
                    timezone = user.timezone
                    locale = user.locale
                    localeOptions = user.localeOptions
                    avatar = user.avatar
                    company = user.company
                    title = user.title
                    emailNotifications = user.emailNotifications
                    lastAccessOn = user.lastAccessOn
                    lastPage = user.lastPage
                    externalId = user.externalId
                }
            }

            call.successResponse(HttpStatusCode.OK, "User Created")
        }

        get {
            val users = call.asyncTransaction {
                UserService.getUsers()
            }

            call.successResponse(HttpStatusCode.OK, users.toList())
        }

        get("/me") {
            call.successResponse(HttpStatusCode.OK, call.user)
        }

        get<UserIds> { user ->

            val listOfIds = user.ids.split(",")
            val users = listOfIds.map {
                call.asyncTransaction {
                    UserService.getUser(it.toInt())
                }
            }

            if (users.size == 1) {
                call.successResponse(HttpStatusCode.OK, users.first())
                return@get
            }

            call.successResponse(HttpStatusCode.OK, users)
        }

        patch<UserId> { userId ->
            val userData = call.receive<UserReceiver>()

            call.asyncTransaction {
                val user = UserService.getUser(userId.id) ?: throw UserNotFoundException("User not found!")

                user.email = userData.email
                user.password = userData.password
                user.status = userData.status
                user.firstName = userData.firstName
                user.lastName = userData.lastName
                user.email = userData.email
                user.password = BCrypt.hashpw(userData.password, BCrypt.gensalt())
                user.token = userData.token
                user.timezone = userData.timezone
                user.locale = userData.locale
                user.localeOptions = userData.localeOptions
                user.avatar = userData.avatar
                user.company = userData.company
                user.title = userData.title
                user.emailNotifications = userData.emailNotifications
                user.lastAccessOn = userData.lastAccessOn
                user.lastPage = userData.lastPage
                user.externalId = userData.externalId

            }
        }
    }
}

fun StatusPages.Configuration.failedUsers() {

}