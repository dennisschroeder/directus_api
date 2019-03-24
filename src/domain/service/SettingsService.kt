package com.directus.domain.service

import com.directus.repository.SettingsRepository
import domain.model.Setting
import org.jetbrains.exposed.sql.SizedIterable

object SettingsService {
    fun getSettings(): SizedIterable<Setting> = SettingsRepository.getAll()
    fun getSetting(id: Int): Setting? = SettingsRepository.getById(id)
}
