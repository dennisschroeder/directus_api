package com.directus.domain.service

import com.directus.domain.model.Setting
import com.directus.repository.SettingsRepository
import org.jetbrains.exposed.sql.SizedIterable

object SettingsService {
    fun getSettings(): SizedIterable<Setting> = SettingsRepository.getAll()
    fun getSetting(id: Int): Setting? = SettingsRepository.getById(id)
    fun deleteSetting(id: Int) = SettingsRepository.remove(id)
}
