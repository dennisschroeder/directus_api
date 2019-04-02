package com.directus

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/reset/{resetToken}") data class ResetPassword(val resetToken: String)

@KtorExperimentalLocationsAPI
@Location("/{ids}") data class UserIds(val ids: String)

@KtorExperimentalLocationsAPI
@Location("/{id}") data class UserId(val id: Int) {
    @Location("/tracking/page") data class TrackingPage(val userId: UserId)
}

@KtorExperimentalLocationsAPI
@Location("/invite/{token}") data class InvitationToken(val token: String)