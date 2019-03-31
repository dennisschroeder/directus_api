package com.directus.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.directus.ConfigService
import com.directus.domain.model.User
import java.util.*
import kotlin.collections.HashMap

object AuthService {
    private const val default = 60_000 * 5// 60 minute
    private const val passwordRequest = 60_000 * 60 * 24 * 7 // 1 week
    private const val invitationRequest = 60_000 * 60 * 24 * 7 // 1 week

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
            withExpiresAt(getExpiration(passwordRequest))
        }


    fun signPasswordRequestToken(user: User, projectKey: String) =
        signToken(projectKey) {
            withClaim("type", "reset_password")
            withClaim("userId", user.id.value)
            withClaim("email", user.email)
            withExpiresAt(getExpiration(passwordRequest))
        }


    fun signInvitationToken(senderId: Int, userID: Int, email: String, projectKey: String) =
        signToken(projectKey) {
            withClaim("type", "invitation")
            withClaim("id", userID)
            withClaim("sender", senderId)
            withClaim("email", email)
            withClaim("date", Date(System.currentTimeMillis()))
            withExpiresAt(getExpiration(invitationRequest))
        }


    fun getExpiration(timeInMilliseconds: Int) = Date(System.currentTimeMillis() + timeInMilliseconds)
    fun getPrivateKeyForProject(projectKey: String) = ConfigService.configs[projectKey]?.auth!!.privateKey
}

