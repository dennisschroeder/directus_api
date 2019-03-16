package com.directus

import com.directus.domain.service.UserService
import com.directus.repository.Database
import domain.model.Setting
import domain.model.Settings
import domain.model.Users
import io.ktor.application.Application
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.boot(testing: Boolean = false) {

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

