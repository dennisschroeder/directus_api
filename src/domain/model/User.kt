package domain.model

import io.ktor.auth.Credential
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Users: IntIdTable() {
    val status = varchar("status", 16).default("draft")
    val firstName = varchar("firstName", 50).nullable()
    val lastName = varchar("lastName", 50).nullable()
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255).nullable()
    val token = varchar("token", 255).nullable()
    val timezone = varchar("timezone", 32).uniqueIndex().default("UTC")
    val locale = varchar("locale", 8).default("en-US")
    val localeOptions = text("localeOptions").nullable()
    val avatar = integer("avatar").nullable()
    val company = varchar("company", 191).nullable()
    val title = varchar("title", 191).nullable()
    val emailNotifications = bool("emailNotifications").default(true)
    val lastAccessOn = datetime("lastAccessOn").nullable()
    val lastPage = varchar("lastPage", 191).nullable()
    val externalId = varchar("externalId", 255).uniqueIndex().nullable()

}

open class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var status by Users.status
    var firstName by Users.firstName
    var lastName by Users.lastName
    var email by Users.email
    var password by Users.password
    var token by Users.token
    var timezone by Users.timezone
    var locale by Users.locale
    var localeOptions by Users.localeOptions
    var avatar by Users.avatar
    var company by Users.company
    var title by Users.title
    var emailNotifications by Users.emailNotifications
    var lastAccesOn by Users.lastAccessOn
    var lastPage by Users.lastPage
    var externalId by Users.externalId
}

class Credentials(val email: String, val password: String): Credential