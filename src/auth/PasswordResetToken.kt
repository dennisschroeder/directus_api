package com.directus.auth

import domain.model.User

class PasswordResetToken(user: User) {
    var token = AuthService.signPasswordRequestToken(user = user)
}