package com.directus.domain.model

import com.directus.jwt.DirectusJWT

class JwtToken (claim: String) {
    var token = DirectusJWT.sign(claim)
}