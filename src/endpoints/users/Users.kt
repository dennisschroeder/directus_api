package com.directus.endpoints.users

import com.directus.*
import com.directus.auth.AuthService
import com.directus.config.ConfigService
import com.directus.domain.model.InvitationMailReceiver
import com.directus.domain.model.User
import com.directus.domain.model.UserReceiver
import com.directus.domain.model.UserStatus
import com.directus.domain.service.UserService
import com.directus.endpoints.auth.exception.UserNotFoundException
import com.directus.endpoints.auth.user
import com.directus.endpoints.exception.BadRequestException
import com.directus.mail.MailService
import com.directus.repository.database.asyncTransaction
import endpoints.auth.exception.InvalidTokenException
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.routing.*
import org.mindrot.jbcrypt.BCrypt

@KtorExperimentalLocationsAPI
fun Route.users() {

    route("/users") {
        post {
            val user = call.receive<UserReceiver>()

            call.asyncTransaction {
                UserService.createUser {
                    email = user.email ?: throw BadRequestException("Email is required!")
                    password = if (user.password != null) BCrypt.hashpw(
                        user.password,
                        BCrypt.gensalt()
                    ) else throw BadRequestException("Password is required!")
                    status = user.status ?: status
                    firstName = user.firstName
                    lastName = user.lastName
                    token = user.token
                    timezone = user.timezone ?: timezone
                    locale = user.locale ?: locale
                    localeOptions = user.localeOptions
                    avatar = user.avatar
                    company = user.company
                    title = user.title
                    emailNotifications = user.emailNotifications ?: emailNotifications
                    lastAccessOn = user.lastAccessOn
                    lastPage = user.lastPage
                    externalId = user.externalId
                }
            }

            call.successResponse(HttpStatusCode.OK, "User Created")
        }

        get {
            val users = call.asyncTransaction {
                UserService.getUsers().toList()
            }

            call.successResponse(HttpStatusCode.OK, users)
        }

        get("/me") {
            call.successResponse(HttpStatusCode.OK, call.user)
        }

        get<UserIds> { user ->
            val listOfIds = user.ids.split(",")
            val users = listOfIds.mapNotNull {
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

            val patchedUser = call.asyncTransaction {
                val user = UserService.getUser(userId.id) ?: throw UserNotFoundException("User not found!")

                user.email = userData.email ?: user.email
                user.status = userData.status ?: user.status
                user.firstName = userData.firstName ?: user.firstName
                user.lastName = userData.lastName ?: user.lastName
                user.password =
                    if (userData.password != null) BCrypt.hashpw(userData.password, BCrypt.gensalt())
                    else user.password
                user.token = userData.token ?: user.token
                user.timezone = userData.timezone ?: user.timezone
                user.locale = userData.locale ?: user.locale
                user.localeOptions = userData.localeOptions ?: user.localeOptions
                user.avatar = userData.avatar ?: user.avatar
                user.company = userData.company ?: user.company
                user.title = userData.title ?: user.title
                user.emailNotifications = userData.emailNotifications ?: user.emailNotifications
                user.lastAccessOn = userData.lastAccessOn ?: user.lastAccessOn
                user.lastPage = userData.lastPage ?: user.lastPage
                user.externalId = userData.externalId ?: user.externalId

                user
            }


            call.successResponse(HttpStatusCode.OK, patchedUser)
        }

        delete<UserIds> { userIds ->
            val listOfIds = userIds.ids.split(",")
            listOfIds.forEach { id ->
                call.asyncTransaction {
                    UserService.deleteUser(id.toInt())
                }
            }

            call.successResponse(HttpStatusCode.OK, "Removed user")
        }

        post("/invite") {
            val invitationUserData = call.receive<InvitationMailReceiver>().email
            val invitingUser = call.user
            val projectKey = call.projectKey

            suspend fun inviteUser(invitationMail: String, invitingUser: User) {

                val invitedUser = call.asyncTransaction {
                    UserService.createUser {
                        status = UserStatus.INVITED.value
                        email = invitationMail
                        password = "dummyPassword"
                    }
                }

                val invitationToken =
                    AuthService.signInvitationToken(
                        invitingUser.id.value,
                        invitedUser.id.value,
                        invitationMail,
                        projectKey
                    )

                val message = MailService.createMessage {
                    from(ConfigService.configs[projectKey]!!.mail.from)
                    withSubject("Your Invitation!")
                    withPlainText("Hello... Here is your invitation token: $invitationToken")
                    to(invitationMail)
                }

                MailService
                    .createTransporter(projectKey)
                    .sendMail(message)
            }

            if (invitationUserData is String) {
                inviteUser(invitationUserData, invitingUser)
                call.successResponse(HttpStatusCode.OK, null)
                return@post
            }

            if (invitationUserData is ArrayList<*>) {
                invitationUserData.mapNotNull { mail ->
                    inviteUser(mail.toString(), invitingUser)
                }

                call.successResponse(HttpStatusCode.OK, null)
                return@post
            }
        }

        post<InvitationToken> { payload ->
            val token = AuthService.verifier(call.projectKey).verify(payload.token)

            if (token.getClaim("type").asString() != UserStatus.INVITED.value) {
                throw InvalidTokenException("Token has wrong type!")
            }

            val invitationUserEmail =
                token.getClaim("email").asString()
                    ?: throw InvalidTokenException("Claim missing!")


            call.asyncTransaction {
                val user = UserService.getUserByEmail(invitationUserEmail)
                    ?: throw UserNotFoundException("Invited user not found!")

                user.status = UserStatus.ACTIVE.value
            }

            call.successResponse(HttpStatusCode.OK, null)
        }

        patch<UserId.TrackingPage> { userId ->
            userId.userId

        }


    }
}