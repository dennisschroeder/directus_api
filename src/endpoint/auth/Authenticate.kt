package com.directus.endpoint.auth

import com.directus.jwt.DirectusJWT
import com.directus.model.*
import io.ktor.application.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import java.util.*
import javax.lang.model.type.NullType

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

        val response = SuccessResponse<Map<String, String>, NullType>(mapOf("token" to DirectusJWT.sign(user.name)))
        call.respond(response)
    }
}

class InvalidCredentialsException(message: String) : RuntimeException(message)