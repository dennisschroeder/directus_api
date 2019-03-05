package com.directus.service

import com.directus.model.DirectusSetting
import com.directus.repository.SettingsRepository

object SettingsService {

    suspend fun getSetting(id: Int): DirectusSetting? {
        return SettingsRepository.getById(id)
    }

}
