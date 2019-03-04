package com.directus.model

import org.jetbrains.exposed.sql.Table

object DirectusUsers: Table() {
    val id = integer("id").primaryKey().autoIncrement()
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

data class User(val name: String, val password: String)

class Credentials(val name: String, val password: String)