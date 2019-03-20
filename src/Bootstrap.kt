package com.directus

import com.directus.auth.AuthConfig
import com.directus.domain.model.DatabaseConfig
import com.directus.domain.service.UserService
import com.directus.repository.Database
import domain.model.Setting
import domain.model.Settings
import domain.model.Users
import io.ktor.application.Application
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.boot(testing: Boolean = false) {

    val config = environment.config

    ConfigService.projectKey = config.propertyOrNull("directus.projektKey")?.getString() ?: "_"

    ConfigService.database = DatabaseConfig(
        type = config.propertyOrNull("directus.database.type")?.getString() ?: "mysql",
        host = config.propertyOrNull("directus.database.host")?.getString() ?: "localhost",
        port = config.propertyOrNull("directus.database.port")?.getString()?.toInt() ?: 3306,
        name = config.propertyOrNull("directus.database.port")?.getString() ?: "directus",
        username = config.propertyOrNull("directus.database.username")?.getString() ?: "root",
        password = config.propertyOrNull("directus.database.password")?.getString() ?: "root"
    )

    ConfigService.auth = AuthConfig(
        secretKey = config.propertyOrNull("directus.auth.secretKey")?.getString()?: "oUa7VfxSkhzHJFmmLKFQ8kThWYPvpd3a",
        publicKey = config.propertyOrNull("directus.auth.publicKey")?.getString()?: "ie2PupjbvRcXs8GQ8yYc8Tw8wAdburbl"
    )

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

