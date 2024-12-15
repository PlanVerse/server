package com.planverse.server.user.service

import com.planverse.server.common.config.security.JwtTokenProvider
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.dto.Jwt
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.util.RedisUtil
import com.planverse.server.mail.service.MailService
import com.planverse.server.user.dto.SignInDTO
import com.planverse.server.user.dto.SignUpDTO
import com.planverse.server.user.repository.UserInfoRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserInfoService(
    val jwtTokenProvider: JwtTokenProvider,
    val userInfoRepository: UserInfoRepository,
    val passwordEncoder: PasswordEncoder,
    val authenticationManagerBuilder: AuthenticationManagerBuilder,
    val redisUtil: RedisUtil,
    val mailService: MailService,
) {
    @Transactional
    fun signUp(signUpDto: SignUpDTO) {
        if (userInfoRepository.existsByEmail(signUpDto.email)) {
            throw BaseException(StatusCode.ALREADY_USE_EMAIL)
        }

        val encodedPassword = passwordEncoder.encode(signUpDto.pwd)
        userInfoRepository.save(signUpDto.toEntity(encodedPassword))

        mailService.sendAuthMail(signUpDto.email)
    }

    fun signIn(signInDTO: SignInDTO): Jwt {
        try {
            // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
            // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
            val authenticationToken = UsernamePasswordAuthenticationToken(signInDTO.email, signInDTO.password)

            // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
            // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
            val authentication: Authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken)

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            return jwtTokenProvider.generateToken(authentication)
        } catch (iae: IllegalArgumentException) {
            throw BaseException(StatusCode.INVALID_TOKEN)
        } catch (e: Exception) {
            throw BaseException(StatusCode.UNAUTHORIZED)
        }
    }

    fun reissueToken(refreshToken: String): Jwt {
        // Refresh Token 검증
        jwtTokenProvider.validateToken(refreshToken)

        // Access Token 에서 Authentication 조회
        val authentication = jwtTokenProvider.getRefreshAuthentication(refreshToken)

        // Redis에서 저장된 Refresh Token 값을 가져옴
        val redisRefreshToken: String = redisUtil.get(authentication.name)
        if (StringUtils.isBlank(redisRefreshToken) || redisRefreshToken == refreshToken) {
            throw BaseException(StatusCode.EXPIRED_TOKEN)
        }

        // 토큰 재발행
        return jwtTokenProvider.generateToken(authentication)
    }

    fun signOut() {
    }
}