package com.directus

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI

@Location("/reset/{resetToken}")
data class ResetPassword(val resetToken: String)