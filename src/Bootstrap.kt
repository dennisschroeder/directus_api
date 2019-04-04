package com.directus

import com.auth0.jwt.algorithms.Algorithm
import com.charleskorn.kaml.Yaml
import com.directus.auth.AuthService
import com.directus.config.ConfigService
import com.directus.config.ProjectConfig
import com.directus.domain.model.*
import com.directus.domain.service.RoleService
import com.directus.domain.service.UserService
import com.directus.repository.database.DatabaseService
import io.ktor.application.Application
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.boot(testing: Boolean = false) {

    val testConfigFile = "api.test.yaml"

    if (testing) {
        val testProjectKey = "test"
        val testConfig =
            File("resources/$testConfigFile").takeIf { it.exists() } ?: throw Exception("$testConfigFile not found")
        ConfigService.configs[testProjectKey] = Yaml.default.parse(ProjectConfig.serializer(), testConfig.readText())

        AuthService.algorithms[testProjectKey] =
            Algorithm.HMAC256(ConfigService.configs[testProjectKey]!!.auth.secretKey)

        val dbSource = DatabaseService.buildSqlConfig(
            host = ConfigService.configs[testProjectKey]!!.database.host,
            port = ConfigService.configs[testProjectKey]!!.database.port,
            dbName = ConfigService.configs[testProjectKey]!!.database.name,
            username = ConfigService.configs[testProjectKey]!!.database.username,
            password = ConfigService.configs[testProjectKey]!!.database.password
        )

        DatabaseService.connections[testProjectKey] = DatabaseService.initMysql(dbSource)


    } else {
        val resources = File("resources/").takeIf { it.exists() } ?: throw Exception("Resource directory not found")
        resources
            .walk()
            .forEach { file ->
                file.takeIf { it.isFile }
                    ?.takeIf { it.name.endsWith(".yaml") && it.name.startsWith("api") && it.name != testConfigFile }
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
    }

    DatabaseService.createTables(
        Users,
        Roles,
        UserRoles,
        Settings,
        Activities,
        Collections,
        Revisions
    )

    launch {
        DatabaseService.connections.forEach { connection ->
            val admin = transaction(connection.value) {
                val role = RoleService.getRoleByName("Administrator")

                when(role) {
                    null -> RoleService.createRole {
                        name = "Administrator"
                        description = "Admins have access to all managed data within the system by default"
                    }
                    else -> role
                }
            }

            val user = transaction(connection.value) {
                val user = UserService.getUserByEmail("admin@example.com")

                when(user) {
                    null -> UserService.createUser {
                        email = "admin@example.com"
                        password = BCrypt.hashpw("password", BCrypt.gensalt())
                        status = UserStatus.ACTIVE.value
                    }
                    else -> user
                }
            }

            transaction(connection.value) {
                user.roles = SizedCollection(admin)
            }
        }
    }
}

