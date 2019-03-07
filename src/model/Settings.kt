package com.directus.model

import org.jetbrains.exposed.sql.ResultRow

object DirectusSettings: AbstractTable() {
    val key = varchar("key", 64)
    val value = text("value")
}

data class DirectusSetting(val id: Int, val key: String, val value: String) {
    companion object Factory {
        fun createFromResultRow(row: ResultRow) = DirectusSetting(
            id = row[DirectusSettings.id],
            key = row[DirectusSettings.key],
            value = row[DirectusSettings.value]
        )
    }
}