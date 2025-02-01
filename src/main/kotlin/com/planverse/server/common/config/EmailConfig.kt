package com.planverse.server.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class EmailConfig(
    @Value("\${spring.mail.host}")
    val host: String,

    @Value("\${spring.mail.port}")
    val port: Int,

    @Value("\${spring.mail.username}")
    val username: String,

    @Value("\${spring.mail.password}")
    val password: String,

    @Value("\${spring.mail.properties.mail.smtp.auth}")
    val auth: Boolean,

    @Value("\${spring.mail.properties.mail.smtp.timeout}")
    val timeout: Int,

    @Value("\${spring.mail.properties.mail.smtp.writetimeout}")
    val writetimeout: Int,

    @Value("\${spring.mail.properties.mail.smtp.connectiontimeout}")
    val connectiontimeout: Int,

    @Value("\${spring.mail.properties.mail.smtp.starttls.enable}")
    val starttlsEnable: Boolean,

    @Value("\${spring.mail.properties.mail.smtp.starttls.required}")
    val starttlsRequired: Boolean,
) {
    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port
        mailSender.username = username
        mailSender.password = password
        mailSender.defaultEncoding = "UTF-8"
        mailSender.javaMailProperties = getMailProperties()

        return mailSender
    }

    private fun getMailProperties(): Properties {
        val properties = Properties()
        properties["mail.smtp.auth"] = auth
        properties["mail.smtp.timeout"] = timeout
        properties["mail.smtp.writetimeout"] = writetimeout
        properties["mail.smtp.connectiontimeout"] = connectiontimeout
        properties["mail.smtp.starttls.enable"] = starttlsEnable
        properties["mail.smtp.starttls.required"] = starttlsRequired

        return properties
    }
}