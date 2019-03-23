package com.directus.mail

import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.email.EmailPopulatingBuilder
import org.simplejavamail.mailer.Mailer
import org.simplejavamail.mailer.MailerBuilder


object MailService {
    var host: String? = null
    var port: Int? = null
    var secure: Boolean? = null
    var username: String? = null
    var password: String? = null
    var from: String? = null

    fun createTransporter(): Mailer {

        val builder = MailerBuilder
            .withSMTPServer(host, port)

        if (secure!!) {
            builder.withSMTPServerUsername(username)
            builder.withSMTPServerPassword(password)
        }

        return builder.buildMailer()
    }

    fun createMessage(): EmailPopulatingBuilder {
        return EmailBuilder.startingBlank().from(from!!)
    }
}