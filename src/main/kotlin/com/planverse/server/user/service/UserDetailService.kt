package com.planverse.server.user.service

import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.user.repository.UserInfoRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserDetailService(
    val userInfoRepository: UserInfoRepository
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        return userInfoRepository.findByEmail(email).orElseThrow {
            BaseException(StatusCode.USER_NOT_FOUND)
        }

//        val user: UserInfoEntity = userInfoRepository.findByEmail(email).orElseThrow {
//            BaseException(StatusCode.USER_NOT_FOUND)
//        }
//        return User.builder()
//            .username(user.email)
//            .password(user.password)
//            .authorities(user.authorities)
//            .roles(user.role)
//            .build()
    }
}