package com.planverse.server.mail.service

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.util.RedisUtil
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailService(
    @Value("\${spring.mail.sender.noreply}")
    val senderNoreply: String,
    @Value("\${spring.mail.sender.notice}")
    val senderNotice: String,

    val javaMailSender: JavaMailSender,
    val redisUtil: RedisUtil,
) {
    private fun createAuthMail(email: String): MimeMessage {
        val number = ((Math.random() * (90000)).toInt() + 100000).toString()
        redisUtil.setWithExpiryMin(email, number, 10)

        val message: MimeMessage = javaMailSender.createMimeMessage()

        try {
            val body = """
                <h3>메일 인증 번호이며 유효기간은 10분 입니다.</h3>
                <h1>$number</h1>
            """.trimIndent()

            message.setFrom(senderNoreply)
            message.setRecipients(MimeMessage.RecipientType.TO, email)
            message.subject = "[ Planverse ] 메일 인증"
            message.setText(body, "UTF-8", "html")
        } catch (e: MessagingException) {
            e.printStackTrace()
        }

        return message
    }

    fun sendAuthMail(email: String) {
        if (!redisUtil.has(email)) {
            val message: MimeMessage = createAuthMail(email)
            javaMailSender.send(message)
        } else {
            throw BaseException(StatusCode.ALREADY_SENT_EMAIL)
        }
    }

    fun mailAuthCheck(email: String, authNumber: String) {
        val redisNumber = redisUtil.get(email)
        if (redisNumber == authNumber) {
            // TODO 임시권한 유저 권한 수정
        }
    }
}
