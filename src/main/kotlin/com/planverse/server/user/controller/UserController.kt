package com.planverse.server.user.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.common.dto.Jwt
import com.planverse.server.user.dto.SignInDTO
import com.planverse.server.user.dto.SignUpDTO
import com.planverse.server.user.service.UserInfoService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/")
class UserController(
    val userInfoService: UserInfoService,
) {

    @PostMapping("/sign-up")
    fun signUp(@Validated @RequestBody signUpDto: SignUpDTO): BaseResponse<Unit> {
        userInfoService.signUp(signUpDto)
        return BaseResponse.success()
    }

    @PostMapping("/sign-in")
    fun signIn(@Validated @RequestBody signInDTO: SignInDTO): BaseResponse<Jwt> {
        val jwt: Jwt = userInfoService.signIn(signInDTO)
        return BaseResponse.success(data = jwt)
    }

    @PostMapping("/refresh")
    fun refresh(@Validated @RequestBody refreshToken: String): BaseResponse<Jwt> {
        val jwt: Jwt = userInfoService.reissueToken(refreshToken)
        return BaseResponse.success(data = jwt)
    }

    // TODO 기능 구현
    @PostMapping("/sign-out")
    fun signOut(): BaseResponse<Unit> {
        return BaseResponse.success()
    }
}