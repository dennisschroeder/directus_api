package integration.util

import com.directus.auth.AuthToken

data class AuthToken(val data: AuthToken)
data class Error(val code: Int, val message: String)
