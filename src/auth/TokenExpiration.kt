package com.directus.auth

enum class TokenExpiration(val date: Int) {
    FIVE_MINUTES(60_000 * 5),
    ONE_NOUR(60_000 * 60),
    ONE_WEEK(60_000 * 60 * 24 * 7 )
}