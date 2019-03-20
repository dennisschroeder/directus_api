package com.directus.domain.model

import com.directus.auth.AuthService
import domain.model.User

class AuthToken (user: User) {
    var token = AuthService.signAuthToken(user)
}