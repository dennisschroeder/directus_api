package com.directus.auth

import domain.model.User

class AuthToken (user: User, projectKey: String) {
    var token: String

    init {
        this.token = AuthService.signAuthToken(user,projectKey)
    }
}