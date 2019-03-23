package com.directus.repository.database

data class DatabaseConfig(
    val type: String? = null,
    val host: String? = null,
    val port: Int? = null,
    val name: String? = null,
    val username: String? = null,
    val password: String? = null
)