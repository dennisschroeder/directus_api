package com.directus.model

import org.jetbrains.exposed.sql.Table

abstract class AbstractTable: Table() {
    val id = integer("id").primaryKey().autoIncrement()
}