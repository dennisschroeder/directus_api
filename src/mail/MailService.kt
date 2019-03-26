package com.directus.mail

import com.directus.ConfigService
import org.simplejavamail.email.Email
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.email.EmailPopulatingBuilder
import org.simplejavamail.mailer.Mailer
import org.simplejavamail.mailer.MailerBuilder


object MailService {

    fun createTransporter(projectKey: String): Mailer {

        val config = ConfigService.configs[projectKey]!!.mail

        val builder = MailerBuilder
            .withSMTPServer(config.host, config.port)

        if (config.secure) {
            builder.withSMTPServerUsername(config.username)
            builder.withSMTPServerPassword(config.password)
        }

        return builder.buildMailer()
    }

    fun createMessage(init: EmailPopulatingBuilder.() -> Unit): Email {
        val message = EmailBuilder.startingBlank()
        message.init()
        return message.buildEmail()
    }
}