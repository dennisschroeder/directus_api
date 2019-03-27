package integration

import com.directus.auth.AuthService
import com.directus.domain.service.UserService

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTest {

    private val projectKey = "_"

    @Test
    fun testPingResponse() = testApp {
        handleRequest(HttpMethod.Get, "server/ping").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("pong", response.content)
        }
    }

    @Test
    fun testRootResponse() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Serving application information soon...", response.content)
        }
    }


    @Test
    fun testAuthService() = testApp {
        handleRequest(HttpMethod.Post, "/$projectKey/auth/authenticate") {
            setBody("""{"email": admin@example.com, "password": password}""")

        }.apply {

            val mockedUser = transaction { UserService.getUserByEmail("admin@example.com")!! }
            val mockedAuthToken = AuthService.signAuthToken(mockedUser, projectKey)

            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(response.content, mockedAuthToken)
        }
    }

}
