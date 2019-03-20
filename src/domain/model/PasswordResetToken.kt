package com.directus.domain.model

import com.directus.auth.AuthService
import domain.model.User

class PasswordResetToken (user: User) {
    var token = AuthService.signPasswordRequestToken(user=user)
}