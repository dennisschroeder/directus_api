package integration

import com.directus.boot
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication

fun testApp(callback: TestApplicationEngine.() -> Unit) {
    withTestApplication({boot(testing = true) }) {

    }
}