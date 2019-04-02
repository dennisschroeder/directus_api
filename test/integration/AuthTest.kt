package integration

import com.directus.auth.AuthService
import com.directus.boot
import com.directus.domain.model.ErrorCode
import com.directus.domain.service.UserService
import com.directus.main
import com.directus.repository.database.DatabaseService
import com.google.gson.Gson
import integration.util.AuthToken
import integration.util.Error
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.mindrot.jbcrypt.BCrypt
import kotlin.test.*

class AuthTest {

    private val pk = "test"
    private val defaultAdminMail = "admin@example.com"
    private val defaultAdminPassword = "password"

    @BeforeTest
    fun boot() = withTestApplication({ boot(testing = true) }) {}

    @Test
    fun testAuthenticate(): Unit =

        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Post, "/$pk/auth/authenticate") {
                addHeader("Content-Type", "application/json")
                setBody(
                    """
                        {
                          "email": $defaultAdminMail,
                          "password": $defaultAdminPassword
                        }
                    """.trimIndent()
                )
            }.run {
                assertEquals(HttpStatusCode.OK, response.status())
                val response = Gson().fromJson<AuthToken>(response.content, AuthToken::class.java)
                val id = AuthService.verifier(pk).verify(response.data.token).getClaim("userId").asInt()
                val user = DatabaseService.transaction(pk) {
                    UserService.getActiveUser(id)
                } ?: fail("User not found")

                assertEquals(defaultAdminMail, user.email)
                assertTrue(BCrypt.checkpw(defaultAdminPassword, user.password))
            }

            handleRequest(HttpMethod.Post, "/$pk/auth/authenticate") {
                addHeader("Content-Type", "application/json")
                setBody(
                    """
                        {
                          "email": $defaultAdminMail,
                          "password": wrongPassword
                        }
                    """.trimIndent()
                )
            }.run {
                val response = Gson().fromJson<Error>(response.content, Error::class.java)

                assertEquals(ErrorCode.INVALID_CREDENTIALS.internalCode, response.code)
                assertEquals("Wrong Credentials", response.message)
            }
        }

    @Test
    fun testRefreshToken(): Unit =

        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Post, "/$pk/auth/refresh") {
                addHeader("Content-Type", "application/json")
                setBody(
                    """
                        {
                          "token": $defaultAdminMail,
                        }
                    """.trimIndent()
                )
            }
        }
}
