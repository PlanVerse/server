package com.planverse.server.user.service

import com.planverse.server.auth.dto.AuthDTO
import com.planverse.server.auth.dto.ReAuthDTO
import com.planverse.server.auth.dto.SignInDTO
import com.planverse.server.auth.dto.SignUpDTO
import com.planverse.server.common.config.security.JwtTokenProvider
import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.constant.SystemRole
import com.planverse.server.common.dto.Jwt
import com.planverse.server.common.exception.BaseException
import com.planverse.server.common.service.TokenBlacklistService
import com.planverse.server.common.util.RedisUtil
import com.planverse.server.mail.service.MailService
import com.planverse.server.user.dto.*
import com.planverse.server.user.entity.UserInfoEntity
import com.planverse.server.user.repository.UserInfoRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class UserInfoService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userInfoRepository: UserInfoRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val mailService: MailService,
    private val tokenBlacklistService: TokenBlacklistService,
) {
    private fun getUserByEmail(email: String): UserInfoEntity {
        return userInfoRepository.findByEmailAndDeleteYn(email, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.USER_NOT_FOUND)
        }
    }

    private fun checkUserRole(user: UserInfoEntity, role: SystemRole): Boolean {
        return user.role == role
    }

    @Transactional
    fun signUp(signUpDto: SignUpDTO): String {
        try {
            if (userInfoRepository.existsByEmailAndDeleteYn(signUpDto.email, Constant.DEL_N)) {
                throw BaseException(StatusCode.ALREADY_USE_EMAIL)
            }

            val key = UUID.randomUUID().toString()
            val encodedPassword = passwordEncoder.encode(signUpDto.pwd)
            userInfoRepository.save(signUpDto.toEntity(key, encodedPassword, SystemRole.ROLE_TEMP_USER))

            RedisUtil.setWithExpiryMin(key, signUpDto.email, 10)

            mailService.sendAuthMail(signUpDto.email, key)

            return key
        } catch (be: BaseException) {
            throw be
        } catch (e: Exception) {
            throw BaseException(StatusCode.FAIL)
        }
    }

    @Transactional
    fun verifyEmail(authDTO: AuthDTO) {
        val email = RedisUtil.get(authDTO.token) ?: throw BaseException(StatusCode.EXPIRED_AUTH_EMAIL_REQUEST)
        if (authDTO.email !== email) {
            StatusCode.BAD_REQUEST
        }

        val user: UserInfoEntity = getUserByEmail(email.toString())
        if (checkUserRole(user, SystemRole.ROLE_TEMP_USER)) {
            user.authentication = true
            user.role = SystemRole.ROLE_USER

            userInfoRepository.save(user)
        } else {
            throw BaseException(StatusCode.ALREADY_AUTH_EMAIL)
        }
    }

    @Transactional
    fun reMail(reAuthDTO: ReAuthDTO): String {
        val user: UserInfoEntity = getUserByEmail(reAuthDTO.email)

        if (checkUserRole(user, SystemRole.ROLE_TEMP_USER)) {
            if (RedisUtil.has(user.key!!)) {
                throw BaseException(StatusCode.ALREADY_SENT_EMAIL)
            } else {
                val key = UUID.randomUUID().toString()
                user.key = key
                userInfoRepository.save(user)
                RedisUtil.setWithExpiryMin(key, reAuthDTO.email, 10)

                return key
            }
        } else {
            throw BaseException(StatusCode.ALREADY_AUTH_EMAIL)
        }
    }

    fun signIn(signInDTO: SignInDTO): Jwt {
        try {
            // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
            // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
            val authenticationToken = UsernamePasswordAuthenticationToken(signInDTO.email, signInDTO.pwd)

            // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
            // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
            val authentication: Authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            return jwtTokenProvider.generateToken(authentication)
        } catch (iae: IllegalArgumentException) {
            throw BaseException(StatusCode.INVALID_TOKEN)
        } catch (e: Exception) {
            throw BaseException(StatusCode.UNAUTHORIZED)
        }
    }

    @Transactional
    fun signOut(userInfo: UserInfo) {
        val accessToken = userInfo.accessToken ?: throw BaseException(StatusCode.UNAUTHORIZED)

        tokenBlacklistService.addTokenBlacklistAndRemoveRefreshToken(accessToken)
    }
}