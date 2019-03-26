package com.directus

import com.auth0.jwt.algorithms.Algorithm
import com.charleskorn.kaml.Yaml
import com.directus.auth.AuthService
import com.directus.config.ProjectConfig
import com.directus.domain.service.UserService
import com.directus.repository.database.DatabaseService
import domain.model.Settings
import domain.model.Users
import io.ktor.application.Application
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.boot(testing: Boolean = false) {

    val resources = File("resources/").takeIf { it.exists() } ?: throw Exception("Resource directory not found")
    resources
        .walk()
        .forEach { file ->
            file.takeIf { it.isFile }
                ?.takeIf { it.name.endsWith(".yaml") && it.name.startsWith("api") }
                ?.apply {
                    val projectKey =
                        if (nameWithoutExtension.substringAfter('.') != "api")
                            nameWithoutExtension.substringAfter('.') else "_"
                    val fileContent = File("${resources.absolutePath}/$name").readText()

                    // Initializing the project settings by project scope
                    ConfigService.configs[projectKey] = Yaml.default.parse(ProjectConfig.serializer(), fileContent)

                    AuthService.algorithms[projectKey] =
                        Algorithm.HMAC256(ConfigService.configs[projectKey]!!.auth.secretKey)

                    DatabaseService.connections[projectKey] = DatabaseService.initMysql(
                        DatabaseService.buildSqlConfig(
                            host = ConfigService.configs[projectKey]!!.database.host,
                            port = ConfigService.configs[projectKey]!!.database.port,
                            dbName = ConfigService.configs[projectKey]!!.database.name,
                            username = ConfigService.configs[projectKey]!!.database.username,
                            password = ConfigService.configs[projectKey]!!.database.password
                        )
                    )
                }
        }

    DatabaseService.createTables(Users, Settings)

    launch {
        DatabaseService.connections.forEach {
            transaction(it.value) {
                val user = UserService.getUserByEmail("admin@example.com")
                if (null === user) {
                    UserService.createUser {
                        email = "admin@example.com"
                        password = BCrypt.hashpw("password", BCrypt.gensalt())
                    }
                }
            }
        }
    }

}

