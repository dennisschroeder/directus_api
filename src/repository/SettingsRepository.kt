package com.directus.repository

import domain.model.Setting
import domain.model.Settings
import repository.RepositoryInterface

object SettingsRepository: RepositoryInterface<Settings> {
    suspend fun getByValue(value: String) = asyncQuery {
        Setting.find { Settings.value eq value }.singleOrNull()
    }

}
