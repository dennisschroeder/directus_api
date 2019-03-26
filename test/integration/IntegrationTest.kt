package integration

import com.directus.boot
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTest {

    @Test
    fun testPingResponse() = testApp {
        withTestApplication({ boot(testing = true) }) {
            handleRequest(HttpMethod.Get, "server/ping").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("pong", response.content)
            }
        }
    }

    @Test
    fun testRootResponse() = testApp {
        withTestApplication({ boot(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Serving application information soon...", response.content)
            }
        }
    }


    @Test
    fun testAuthService() = testApp {
        withTestApplication({boot(testing = true) }) {

        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {

    }
}
