package com.directus

import com.directus.auth.AuthConfig
import com.directus.domain.model.DatabaseConfig


object ConfigService {
 var database: DatabaseConfig? = null
 var projectKey: String? = null
 var auth: AuthConfig? = null
}
