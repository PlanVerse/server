package com.planverse.server.mail.service

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.util.RedisUtil
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MailService(
    @Value("\${spring.mail.sender.noreply}")
    private val senderNoreply: String,
    @Value("\${spring.mail.sender.notice}")
    private val senderNotice: String,

    private val javaMailSender: JavaMailSender,
) {
    @Transactional
    fun createAuthMail(email: String, key: String): MimeMessage {
        val message: MimeMessage = javaMailSender.createMimeMessage()

        try {
            val body = """
                <h2>안녕하세요. Planverse에 가입해주셔서 감사합니다.</h3>
                <h3>아래 링크를 클릭하여 이메일 인증을 완료해주시기 바랍니다.</h3>
                <h3>유효기간은 10분 입니다.</h4>
                <h2>$key</h2>
            """.trimIndent()

            message.setFrom(senderNoreply)
            message.setRecipients(MimeMessage.RecipientType.TO, email)
            message.subject = "[ Planverse ] 회원가입 이메일 인증"
            message.setText(body, "UTF-8", "html")
        } catch (e: MessagingException) {
            e.printStackTrace()
        }

        return message
    }

    fun sendAuthMail(email: String, key: String) {
        val message: MimeMessage = createAuthMail(email, key)
        try {
            javaMailSender.send(message)
        } catch (me: MailException) {
            throw BaseException(StatusCode.CANNOT_SENT_EMAIL)
        }
        RedisUtil.setWithExpiryMin(key, email, 10)
    }
}
