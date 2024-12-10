package com.planverse.server.mail.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.mail.service.MailService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mail/")
class MailController(
    val mailService: MailService,
) {
    @GetMapping("/auth/{email}")
    fun mailAuth(@PathVariable("email") email: String): BaseResponse<Unit> {
        mailService.sendAuthMail(email)
        return BaseResponse.success()
    }

    // 인증번호 일치여부 확인
    @PostMapping("/auth-check/{email}/{authNumber}")
    fun mailAuthCheck(@PathVariable("email") email: String, @PathVariable("authNumber") authNumber: String): BaseResponse<Unit> {
        mailService.mailAuthCheck(email, authNumber)

        return BaseResponse.success()
    }
}