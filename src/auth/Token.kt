package com.directus.auth

data class AuthToken (val token: String)

data class PasswordResetToken(val token: String)