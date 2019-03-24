package com.directus.repository

import domain.model.Setting
import domain.model.Settings
import repository.RepositoryInterface

object SettingsRepository: RepositoryInterface<Setting> {
    override fun getById(id: Int) = Setting.findById(id)
    override fun getAll() = Setting.all()

    fun getByValue(value: String) = Setting.find { Settings.value eq value }.singleOrNull()

}
