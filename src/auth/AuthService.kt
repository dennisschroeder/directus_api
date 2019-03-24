package com.directus.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.directus.ConfigService
import domain.model.User
import java.util.*
import kotlin.collections.HashMap

object AuthService {
    private const val validityInMs = 60_000 * 5 // 5 minute

    // We need one algorithm per project.
    // The algorithms are set in the boot module
    val algorithms = HashMap<String,Algorithm>()

    fun verifier(projectKey: String): JWTVerifier = JWT.require(algorithms[projectKey]).build()

    fun signAuthToken(user: User, projectKey: String) = JWT.create()
        .withClaim("userId", user.id.value)
        .withClaim("type", "auth")
        .withClaim("key", getPrivateKeyForProject(projectKey))
        .withExpiresAt(getExpiration())
        .sign(algorithms[projectKey])!!

    fun signPasswordRequestToken(user: User, projectKey: String) = JWT.create()
        .withClaim("email", user.email)
        .withClaim("userId", user.id.value)
        .withClaim("type", "reset_password")
        .withExpiresAt(getExpiration())
        .sign(algorithms[projectKey])!!

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
    private fun getPrivateKeyForProject(projectKey: String) = ConfigService.configs[projectKey]?.auth!!.privateKey
}

