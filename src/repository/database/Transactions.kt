package com.directus.repository.database

import io.ktor.application.ApplicationCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

fun <Q> ApplicationCall.transaction(query: () -> Q) {
    org.jetbrains.exposed.sql.transactions.transaction(dbConnection) { query() }
}

suspend fun <Q> ApplicationCall.asyncTransaction(query: () -> Q) = withContext(Dispatchers.IO) {
    transaction(dbConnection) { query() }
}