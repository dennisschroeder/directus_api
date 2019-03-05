package com.directus.repository

import com.directus.model.DirectusSetting
import com.directus.model.DirectusSettings
import repository.RepositoryInterface
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

object SettingsRepository: RepositoryInterface<DirectusSettings, DirectusSetting> {
    override val table =  DirectusSettings

    suspend fun getById(id: Int): DirectusSetting? = dbQuery {
        table.select {
            (table.id eq id)
        }.mapNotNull { mapToModel(it) }.singleOrNull()
    }

    override fun mapToModel(row: ResultRow) = DirectusSetting(
        id = row[DirectusSettings.id],
        key = row[DirectusSettings.key],
        value = row[DirectusSettings.value]
    )

}

