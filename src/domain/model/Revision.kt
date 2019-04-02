package com.directus.domain.model

import com.google.gson.annotations.JsonAdapter
import com.rnett.exposedgson.ExposedGSON
import com.rnett.exposedgson.ExposedTypeAdapter
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Revisions: IntIdTable("directus_revision") {
    val activity = reference("activity", Activities)
    val collection = reference("collection", Collections)
    val item = varchar("item", 255)
    val data = long("data").nullable()
    val delta = long("delta")
    val parentCollection = reference("parent_collection", Collections)
    val parentItem = varchar("parent_item", 255)
    val parentChanged = bool("parent_changed")

}

@JsonAdapter(ExposedTypeAdapter::class)
@ExposedGSON.JsonDatabaseIdField("id")
open class Revision(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Revision>(Revisions)

    var activity by Activity referencedOn Activities.id
    var collection by Collection referencedOn Collections.collection
    var item by Revisions.item
    var data by Revisions.data
    var delta by Revisions.delta
    var parentCollection by Collection referencedOn Collections.collection

}