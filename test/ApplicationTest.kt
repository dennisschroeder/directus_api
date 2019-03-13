package com.directus

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO DENNIS!", response.content)
            }
        }
    }

    @Test
    fun testLogin() {
        withTestApplication({ main(testing = true) }) {
            val credentials = """
                                {
                                   "name":"test",
                                   "password": "test"
                                }
                            """

            val call = handleRequest(HttpMethod.Post, "/auth/authenticate") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(credentials)
            }

            call.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(
                    "token",
                    response.content
                )
            }
        }
    }
}
