package com.directus.auth


data class AuthConfig(
    val secretKey: String? = null,
    val publicKey: String? = null
    )