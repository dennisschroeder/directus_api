package com.directus.domain.model

data class DatabaseConfig(
    val type: String,
    val host: String,
    val port: Int,
    val name: String,
    val username: String,
    val password: String
)