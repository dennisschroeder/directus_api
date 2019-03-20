package com.directus.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.directus.ConfigService
import domain.model.User
import java.util.*

object AuthService {
    private const val validityInMs = 36_000_00 * 1 // 60 minutes
    private val algorithm: Algorithm = Algorithm.HMAC256(ConfigService.auth!!.secretKey)
    val verifier: JWTVerifier = JWT.require(algorithm).build()

    fun signAuthToken(user: User) = JWT.create()
        .withClaim("userId", user.id.value)
        .withClaim("type", "auth")
        .withClaim("key", ConfigService.auth?.publicKey)
        .withExpiresAt(getExpiration())
        .sign(algorithm)!!

    fun signPasswordRequestToken(user: User) = JWT.create()
        .withClaim("email", user.email)
        .withClaim("userId", user.id.value)
        .withClaim("type", "reset_password")
        .withExpiresAt(getExpiration())
        .sign(algorithm)!!

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}

