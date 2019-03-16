package com.directus.domain.model

import io.ktor.auth.Credential

data class Credentials(val email: String, val password: String) : Credential