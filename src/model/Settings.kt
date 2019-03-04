package com.directus.model

import org.jetbrains.exposed.sql.Table

object DirectusSettings: Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val key = varchar("key", 64)
    val value = text("value")
}

data class DirectusSetting(val id: Int, val key: String, val value: String)