package com.directus

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.reflect.full.memberProperties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApplicationTest {

    @Test
    fun testPingResponse() = testApp  {
        withTestApplication({ boot(testing = true) }) {
            handleRequest(HttpMethod.Get, "server/ping").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("pong", response.content)
            }
        }
    }

    @Test
    fun testConfigValuesNotNull() = withTestApplication({ boot(testing = true) }) {
        val config = ConfigService

        config.database::class.memberProperties.forEach { assertNotNull(it) }
        config.auth::class.memberProperties.forEach { assertNotNull(it) }
        config.mail::class.memberProperties.forEach { assertNotNull(it) }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {

    }
}
