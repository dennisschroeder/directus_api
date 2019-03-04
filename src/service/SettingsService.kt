package com.directus.service

import com.directus.model.DirectusSetting
import com.directus.model.DirectusSettings
import com.directus.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

class SettingsService {
    suspend fun getSetting(id: Int): DirectusSetting? = dbQuery {
        DirectusSettings.select {
            (DirectusSettings.id eq id)
        }.mapNotNull { toSetting(it) }.singleOrNull()
    }

    private fun toSetting(row: ResultRow): DirectusSetting = DirectusSetting(
            id = row[DirectusSettings.id],
            key = row[DirectusSettings.key],
            value = row[DirectusSettings.value]
        )


}
