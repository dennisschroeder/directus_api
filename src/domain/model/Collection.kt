package com.directus.domain.model

import com.google.gson.annotations.JsonAdapter
import com.rnett.exposedgson.ExposedGSON
import com.rnett.exposedgson.ExposedTypeAdapter
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Collections: IntIdTable("directus_collection") {
    val collection = varchar("collection", 64).primaryKey()
    val managed = bool("managed").default(true)
    val hidden = bool("hidden").default(false)
    val single = bool("single").default(false)
    val icon = varchar("icon", 30).nullable()
    val note = varchar("note", 255).nullable()
    val translation = text("translation")
}

@JsonAdapter(ExposedTypeAdapter::class)
@ExposedGSON.JsonDatabaseIdField("id")
open class Collection(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Collection>(Collections)

    var collection by Collections.collection
    var managed by Collections.managed
    var hidden by Collections.hidden
    var single by Collections.single
    var icon by Collections.icon
    var note by Collections.note
    var translation by Collections.translation

}