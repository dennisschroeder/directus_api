package com.directus.repository.database

import com.directus.projectKey
import io.ktor.application.ApplicationCall

fun <Q> ApplicationCall.transaction(query: () -> Q) = DatabaseService.transaction(projectKey) {query()}
suspend fun <Q> ApplicationCall.asyncTransaction(query: () -> Q) = DatabaseService.asyncTransaction(projectKey) {query()}
