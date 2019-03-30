package com.directus.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.directus.ConfigService
import com.directus.domain.model.User
import java.util.*
import kotlin.collections.HashMap

object AuthService {
    private const val default = 60_000 * 60 // 60 minute
    private const val passwordRequest = 60_000 * 60 * 24 * 7 // 1 week
    private const val invitationRequest = 60_000 * 60 * 24 * 7 // 1 week

    // We need one algorithm per project.
    // The algorithms are set in the boot module
    val algorithms = HashMap<String, Algorithm>()

    fun verifier(projectKey: String): JWTVerifier = JWT.require(algorithms[projectKey]).build()

    fun signAuthToken(userID: Int, projectKey: String) = JWT.create()
        .withClaim("userId", userID)
        .withClaim("type", "auth")
        .withClaim("key", getPrivateKeyForProject(projectKey))
        .withExpiresAt(getExpiration(passwordRequest))
        .sign(algorithms[projectKey])!!

    fun signPasswordRequestToken(user: User, projectKey: String) = JWT.create()
        .withClaim("email", user.email)
        .withClaim("userId", user.id.value)
        .withClaim("type", "reset_password")
        .withExpiresAt(getExpiration(passwordRequest))
        .sign(algorithms[projectKey])!!

    fun signInvitationToken(userID: Int, email: String, projectKey: String) = JWT.create()
        .withClaim("type", "invitation")
        .withClaim("sender", userID)
        .withClaim("date", Date(System.currentTimeMillis()))
        .withClaim("email", email)
        .withExpiresAt(getExpiration(invitationRequest))
        .sign(algorithms[projectKey])!!

    private fun getExpiration(timeInMilliseconds: Int) = Date(System.currentTimeMillis() + timeInMilliseconds)
    private fun getPrivateKeyForProject(projectKey: String) = ConfigService.configs[projectKey]?.auth!!.privateKey
}

