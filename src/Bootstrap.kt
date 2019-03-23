package com.directus

import com.charleskorn.kaml.Yaml
import com.directus.config.ProjectConfig
import com.directus.domain.service.UserService
import com.directus.repository.database.Database
import domain.model.Setting
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
                ?.takeIf { it.name.endsWith(".yaml") }
                ?.takeIf { it.name.startsWith("api") }
                ?.apply {
                    val projectKey =
                        if (nameWithoutExtension.substringAfter('.') != "api")
                            nameWithoutExtension.substringAfter('.') else "_"
                    val fileContent = File("${resources.absolutePath}/$name").readText()
                    ConfigService.configs[projectKey] = Yaml.default.parse(ProjectConfig.serializer(), fileContent)
                }
        }



    Database.initMysql()
    Database.createTables(Users, Settings)

    transaction {
        Setting.new {
            key = "project_name"
            value = "Directus"
        }
    }

    launch {

        val user = UserService.getUserByEmail("admin@example.com")

        if (null === user) {
            UserService.createUser {
                email = "admin@example.com"
                password = BCrypt.hashpw("password", BCrypt.gensalt())
            }
        }
    }
}

