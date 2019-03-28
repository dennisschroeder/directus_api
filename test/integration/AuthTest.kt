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

class AuthTest {

    private val projectKey = "_"
    private val defaultAdminMail = "admin@example.com"

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
    fun testAuthenticate() = testApp {
        handleRequest(HttpMethod.Post, "/$projectKey/auth/authenticate") {
            setBody("""{"email": $defaultAdminMail, "password": password}""")

        }.apply {

            val mockedUser = transaction { UserService.getUserByEmail(defaultAdminMail)!! }
            val mockedAuthToken = AuthService.signAuthToken(mockedUser, projectKey)

            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(response.content, mockedAuthToken)
        }
    }

    @Test
    fun testRefreshToken() = testApp {
        handleRequest(HttpMethod.Post, "/$projectKey/auth/refresh") {
            val mockedUser = transaction { UserService.getUserByEmail(defaultAdminMail)!! }
            val mockedAuthToken = AuthService.signAuthToken(mockedUser, projectKey)

            setBody("""{"token": $mockedAuthToken}""")

        }.apply {
            val mockedUser = transaction { UserService.getUserByEmail(defaultAdminMail)!! }
            val mockedAuthToken = AuthService.signAuthToken(mockedUser, projectKey)

            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(response.content, mockedAuthToken)
        }
    }

    @Test
    fun testRequestNewPassword() = testApp {
        handleRequest(HttpMethod.Post, "/$projectKey/auth/refresh") {

            setBody("""{"email": $defaultAdminMail}""")

        }.apply {
            val mockedUser = transaction { UserService.getUserByEmail(defaultAdminMail)!! }
            val mockedAuthToken = AuthService.signAuthToken(mockedUser, projectKey)

            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(response.content, mockedAuthToken)
        }
    }

}
