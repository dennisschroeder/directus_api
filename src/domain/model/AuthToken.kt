package com.directus.domain.model

import com.directus.jwt.DirectusJWT
import domain.model.User

class AuthToken (user: User) {
    var token = DirectusJWT.signAuthToken(user)
}