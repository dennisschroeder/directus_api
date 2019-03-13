package com.directus
import com.directus.domain.service.DatabaseFactory
import com.directus.domain.service.UserService
import domain.model.Setting
import domain.model.Settings
import domain.model.User
import domain.model.Users
import io.ktor.application.Application
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.boot(testing: Boolean = false) {

    DatabaseFactory.init()
    DatabaseFactory.createTables(Users, Settings)

    transaction {
        Setting.new {
            key = "project_name"
            value = "Directus"
        }


        val newUser = UserService.createBuilder()
        newUser.new {
            email = "foo@bar.de"
            password = BCrypt.hashpw("dreictus", BCrypt.gensalt())
        }
    }

    val  pwd = BCrypt.hashpw("directus", BCrypt.gensalt())

    val user = transaction {
        User.new {
            email = "admin@admin.de"
            password = pwd
        }
    }

}

