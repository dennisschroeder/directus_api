package com.directus.mail

data class MailConfig (
    var host: String? = null,
    var port: Int? = null,
    var secure: Boolean? = null,
    var username: String? = null,
    var password: String? = null,
    var from: String? = null
)