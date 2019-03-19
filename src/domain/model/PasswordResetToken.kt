package com.directus.domain.model

import com.directus.jwt.DirectusJWT
import domain.model.User

class PasswordResetToken (user: User) {
    var token = DirectusJWT.signPasswordRequestToken(user=user)
}