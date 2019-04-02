package com.directus.domain.model

import com.google.gson.annotations.JsonAdapter
import com.rnett.exposedgson.ExposedGSON
import com.rnett.exposedgson.ExposedTypeAdapter
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Activities: IntIdTable("directus_activity") {
    val action = varchar("activity", 45)
    val actionBy = reference("action_by", Users)
    val actionOn = datetime("action_on")
    val ip = varchar("ip", 50)
    val userAgent = varchar("user_agent", 255)
    val collection = varchar("collection", 64)
    val item = varchar("item", 255)
    val editedOn = datetime("edited_on").nullable()
    val comment = text("comment").nullable()
    val commentDeletedOn = datetime("comment_deleted_on").nullable()
}

@JsonAdapter(ExposedTypeAdapter::class)
@ExposedGSON.JsonDatabaseIdField("id")
open class Activity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Activity>(Activities)

    var action by Activities.action
    var actionBy by User referencedOn Users.id
    var actionOn by Activities.actionOn
    var ip by Activities.ip
    var userAgent by Activities.userAgent
    var collection by Activities.collection
    var item by Activities.item
    var editedOn by Activities.editedOn
    var comment by Activities.comment
    var commentDeletedOn by Activities.commentDeletedOn


}