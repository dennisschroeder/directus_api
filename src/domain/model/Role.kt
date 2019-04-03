package com.directus.domain.model

import com.google.gson.annotations.JsonAdapter
import com.rnett.exposedgson.ExposedGSON
import com.rnett.exposedgson.ExposedTypeAdapter
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Roles : IntIdTable("directus_roles") {
    val name = varchar("name", 100).uniqueIndex()
    val description = varchar("description", 500).nullable()
    val ipWhitelist = text("ip_whitelist").nullable()
    val navBlacklist = text("nav_blacklist").nullable()
    val externalId = varchar("external_id", 255).uniqueIndex().nullable()
}

@JsonAdapter(ExposedTypeAdapter::class)
@ExposedGSON.JsonDatabaseIdField("id")
open class Role(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Role>(Roles)

    var name by Roles.name
    var description by Roles.description
    var ipWhitelist by Roles.ipWhitelist
    var navBlacklist by Roles.navBlacklist
    var externalId by Roles.externalId
}

data class RoleReceiver(
    val name: String?,
    val description: String?,
    val ipWhitelist: String?,
    val navBlacklist: String?
)