package com.directus.config

import kotlinx.serialization.Serializable

@Serializable
data class ProjectConfig (val database: Database, val mail: Mail, val auth: Auth)

@Serializable
data class Database (
    val type: String,
    val host: String,
    val port: Int,
    val name: String,
    val username: String,
    val password: String
)

@Serializable
data class Mail (
    val host: String,
    val port: Int,
    val secure: Boolean,
    val username: String,
    val password: String,
    val from: String
)

@Serializable
data class Auth(
    val secretKey: String,
    val privateKey: String
)
