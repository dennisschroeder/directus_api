package com.directus.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.directus.config.ConfigService
import com.directus.domain.model.User
import com.directus.domain.model.UserStatus
import java.util.*
import kotlin.collections.HashMap

object AuthService {

    // We need one algorithm per project.
    // The algorithms are set in the boot module
    val algorithms = HashMap<String, Algorithm>()

    fun verifier(projectKey: String): JWTVerifier = JWT.require(algorithms[projectKey]).build()

    fun signToken(projectKey: String, init: JWTCreator.Builder.() -> Unit): String {
        val token = JWT.create()
        token.init()
        return token
            .withClaim("key", getPrivateKeyForProject(projectKey))
            .sign(algorithms[projectKey])!!

    }

    fun signAuthToken(userID: Int, projectKey: String) =
        signToken(projectKey) {
            withClaim("type", "auth")
            withClaim("userId", userID)
            withExpiresAt(getExpiration(TokenExpiration.FIVE_MINUTES.date))
        }


    fun signPasswordRequestToken(user: User, projectKey: String) =
        signToken(projectKey) {
            withClaim("type", "reset_password")
            withClaim("userId", user.id.value)
            withClaim("email", user.email)
            withExpiresAt(getExpiration(TokenExpiration.ONE_WEEK.date))
        }


    fun signInvitationToken(senderId: Int, userID: Int, email: String, projectKey: String) =
        signToken(projectKey) {
            withClaim("type", UserStatus.INVITED.value)
            withClaim("id", userID)
            withClaim("sender", senderId)
            withClaim("email", email)
            withClaim("date", Date(System.currentTimeMillis()))
            withExpiresAt(getExpiration(TokenExpiration.ONE_WEEK.date))
        }


    fun getExpiration(timeInMilliseconds: Int) = Date(System.currentTimeMillis() + timeInMilliseconds)
    fun getPrivateKeyForProject(projectKey: String) = ConfigService.configs[projectKey]?.auth!!.privateKey


}

