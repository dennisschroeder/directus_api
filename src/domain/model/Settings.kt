package domain.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Settings: IntIdTable("directus_setting") {
    val key = varchar("key", 64)
    val value = text("value")
}

open class Setting(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Setting>(Settings)

    var key by Settings.key
    var value by Settings.value
}