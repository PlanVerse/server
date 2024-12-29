package com.planverse.server.user.controller

import com.planverse.server.common.dto.BaseResponse
import com.planverse.server.common.dto.Jwt
import com.planverse.server.user.dto.AuthDTO
import com.planverse.server.user.dto.ReAuthDTO
import com.planverse.server.user.dto.SignInDTO
import com.planverse.server.user.dto.SignUpDTO
import com.planverse.server.user.service.UserInfoService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth/")
class UserController(
    private val userInfoService: UserInfoService,
) {

    @PostMapping("/sign-up")
    fun signUp(@Validated @RequestBody signUpDto: SignUpDTO): BaseResponse<String> {
        val key: String = userInfoService.signUp(signUpDto)
        return BaseResponse.success(key)
    }

    @PostMapping("/verify")
    fun verifyEmail(@Validated @RequestBody authDTO: AuthDTO): BaseResponse<Unit> {
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

    // TODO 토큰을 블랙리스트에 넣거나 프론트에서 처리해야함 :: 위변조 가능성있으니 만료시키는 것도 방법일듯함
    @PostMapping("/sign-out")
    fun signOut(): BaseResponse<Unit> {
        userInfoService.signOut()
        return BaseResponse.success()
    }
}