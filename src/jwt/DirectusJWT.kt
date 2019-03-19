@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection")

package com.directus.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import domain.model.User
import java.util.*

@Suppress("SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection")
object DirectusJWT {
    private const val secret = "aksdjw390dsfasdköasdköalsdkasöld"
    private const val validityInMs = 36_000_00 * 1 // 1 hour
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)
    val verifier: JWTVerifier = JWT.require(algorithm).build()

    fun signAuthToken(user: User) = JWT.create()
        .withClaim("userId", user.id.value)
        .withClaim("type", "auth")
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

