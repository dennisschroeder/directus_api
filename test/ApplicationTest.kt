package com.directus

import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO DENNIS!", response.content)
            }
        }
    }

    @Test
    fun testLogin() {
        withTestApplication({ module(testing = true) }) {
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
