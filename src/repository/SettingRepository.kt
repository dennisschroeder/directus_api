package com.directus.repository

import com.directus.domain.model.Setting
import com.directus.domain.model.Settings
import org.jetbrains.exposed.sql.deleteWhere
import repository.RepositoryInterface

object SettingRepository: RepositoryInterface<Setting> {
    override fun remove(id: Int) = Settings.deleteWhere { Settings.id eq id }

    override fun getById(id: Int) = Setting.findById(id)
    override fun getAll() = Setting.all()

    fun getByValue(value: String) = Setting.find { Settings.value eq value }.singleOrNull()

}
