package com.directus.domain.model

import com.google.gson.annotations.JsonAdapter
import com.rnett.exposedgson.ExposedGSON
import com.rnett.exposedgson.ExposedTypeAdapter
import io.ktor.auth.Principal
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt

object Users : IntIdTable("directus_user") {
    val status = varchar("status", 16).default(UserStatus.DRAFT.value)
    val firstName = varchar("first_name", 50).nullable()
    val lastName = varchar("last_name", 50).nullable()
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val token = varchar("token", 255).nullable()
    val timezone = varchar("timezone", 32).default("UTC")
    val locale = varchar("locale", 8).default("en-US")
    val localeOptions = text("locale_options").nullable()
    val avatar = integer("avatar").nullable()
    val company = varchar("company", 191).nullable()
    val title = varchar("title", 191).nullable()
    val emailNotifications = bool("email_notifications").default(true)
    val lastAccessOn = datetime("last_access_on").nullable()
    val lastPage = varchar("last_page", 191).nullable()
    val externalId = varchar("external_id", 255).uniqueIndex().nullable()

}

@JsonAdapter(ExposedTypeAdapter::class)
@ExposedGSON.JsonDatabaseIdField("id")
open class User(id: EntityID<Int>) : IntEntity(id), Principal {
    companion object : IntEntityClass<User>(Users)

    var status by Users.status
    var firstName by Users.firstName
    var lastName by Users.lastName
    var email by Users.email

    @ExposedGSON.Ignore
    var password by Users.password

    @ExposedGSON.Ignore
    var token by Users.token

    var timezone by Users.timezone
    var locale by Users.locale
    var localeOptions by Users.localeOptions
    var avatar by Users.avatar
    var company by Users.company
    var title by Users.title
    var emailNotifications by Users.emailNotifications
    var lastAccessOn by Users.lastAccessOn
    var lastPage by Users.lastPage
    var externalId by Users.externalId

    fun authenticate(rawPassword: String) = BCrypt.checkpw(rawPassword, password)
}

object UserRoles : Table("directus_user_roles") {
    var user = reference("user", Users).primaryKey(0)
    var role = reference("role", Roles).primaryKey(1)
}

data class UserReceiver (
    val status: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val password: String?,
    val token: String?,
    val timezone: String?,
    val locale: String?,
    val localeOptions: String?,
    val avatar: Int?,
    val company: String?,
    val title: String?,
    val emailNotifications: Boolean?,
    val lastAccessOn: DateTime?,
    val lastPage: String?,
    val externalId: String?

    )

data class InvitationMailReceiver(val email: Any)

data class TrackUserReceiver(val last_page: String)

enum class UserStatus(val value: String) {
    DRAFT("draft"),
    INVITED("invited"),
    ACTIVE("active"),
    SUSPENDED("suspended"),
    DELETED("deleted")

}