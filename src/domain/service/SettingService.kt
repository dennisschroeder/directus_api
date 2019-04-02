package com.directus.domain.service

import com.directus.domain.model.Setting
import com.directus.repository.SettingRepository
import org.jetbrains.exposed.sql.SizedIterable

object SettingService {
    fun getSettings(): SizedIterable<Setting> = SettingRepository.getAll()
    fun getSetting(id: Int): Setting? = SettingRepository.getById(id)
    fun deleteSetting(id: Int) = SettingRepository.remove(id)
}
