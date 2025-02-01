package com.planverse.server.user.service

import com.planverse.server.common.constant.Constant
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
    private val userInfoRepository: UserInfoRepository
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        return userInfoRepository.findByEmailAndDeleteYn(email, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.USER_NOT_FOUND)
        }
    }
}