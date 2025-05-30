package com.planverse.server.auth.controller

import com.planverse.server.auth.dto.AuthDTO
import com.planverse.server.auth.dto.ReAuthDTO
import com.planverse.server.auth.dto.SignInDTO
import com.planverse.server.auth.dto.SignUpDTO
import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.common.dto.Jwt
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.user.service.UserInfoService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/")
class AuthController(
    private val userInfoService: UserInfoService,
) {

    @PostMapping("/sign-up")
    fun signUp(@Validated @RequestBody signUpDto: SignUpDTO): BaseResponse<String> {
        val key: String = userInfoService.signUp(signUpDto)
        return BaseResponse.success(key)
    }

    @PostMapping("/verify")
    fun verifyEmail(@Validated @RequestBody authDTO: AuthDTO): BaseResponse<Any> {
        userInfoService.verifyEmail(authDTO)
        return BaseResponse.success()
    }

    @PostMapping("/re-mail")
    fun reMail(@Validated @RequestBody reAuthDTO: ReAuthDTO): BaseResponse<String> {
        val key: String = userInfoService.reMail(reAuthDTO)
        return BaseResponse.success(key)
    }

    @PostMapping("/sign-in")
    fun signIn(@Validated @RequestBody signInDTO: SignInDTO): BaseResponse<Jwt> {
        val jwt: Jwt = userInfoService.signIn(signInDTO)
        return BaseResponse.success(data = jwt)
    }

    @PostMapping("/sign-out")
    fun signOut(userInfo: UserInfo): BaseResponse<Any> {
        userInfoService.signOut(userInfo)
        return BaseResponse.success()
    }
}