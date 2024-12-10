package com.planverse.server.common.constant

import org.springframework.http.HttpStatus

enum class StatusCode(
    val code: String,
    val message: String,
    val httpStatus: HttpStatus,
) {
    SUCCESS("0000", "성공", HttpStatus.OK),

    BAD_REQUEST("1000", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_USE_EMAIL("1001", "이미 사용 중인 이메일 입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND("1002", "찾을 수 없는 정보입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_SENT_EMAIL("1003", "이미 메일이 전송되었습니다. 스팸메일함 또는 메일주소를 확인하여주세요.", HttpStatus.BAD_REQUEST),

    NO_AUTHORITY("2000", "권한 정보가 없습니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("2001", "인증 정보가 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("2002", "신뢰할 수 없는 정보입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("2003", "존재하지 않거나 만료된 로그인 정보입니다. 다시 로그인해주세요.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("2004", "변조된 로그인 정보입니다. 다시 로그인해주세요.", HttpStatus.UNAUTHORIZED),
    LOGIN_FAIL("2005", "로그인에 실패하였습니다. 다시 로그인해주세요.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("2005", "사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),

    FAIL("9999", "오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    companion object {
        private val map = entries.associateBy { it.code }
        infix fun from(code: String) = map[code] ?: FAIL
        operator fun get(code: String) = map[code] ?: FAIL
    }
}