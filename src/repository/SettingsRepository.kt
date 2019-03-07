package com.directus.repository

import com.directus.model.DirectusSetting
import com.directus.model.DirectusSettings
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import repository.RepositoryInterface

object SettingsRepository: RepositoryInterface<DirectusSettings, DirectusSetting> {
    override val table =  DirectusSettings

    override suspend fun getAll(): Collection<DirectusSetting> {
        return table.selectAll().mapNotNull { null }
    }

    override suspend fun getById(id: Int): DirectusSetting? = dbQuery {
        table.select {
            (table.id eq id)
        }.mapNotNull { DirectusSetting.createFromResultRow(it) }.singleOrNull()
    }
}

