package com.directus.domain.service

import domain.model.Setting
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction

object SettingsService {

    fun getSettings(): SizedIterable<Setting> = transaction { Setting.all() }
    fun getSetting(id: Int): Setting? = transaction { Setting.findById(id) }



}
