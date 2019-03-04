package com.directus.endpoint.auth

import com.directus.jwt.DirectusJWT
import com.directus.model.*
import io.ktor.application.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import java.util.*

fun Route.authenticate() {
    post("/authenticate") {
        val users = Collections.synchronizedMap(
            listOf(User("test", "test"))
                .associateBy { it.name }
                .toMutableMap()
        )
        val post = call.receive<Credentials>()
        val user = users[post.name]

        if (user?.password != post.password) throw InvalidCredentialsException("Invalid credentials")
        call.respond(mapOf("token" to DirectusJWT.sign(user.name)))
    }
}

class InvalidCredentialsException(message: String) : RuntimeException(message)