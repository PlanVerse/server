package com.planverse.server.common.exception

import com.planverse.server.common.constant.StatusCode

class BaseException(
    val status: StatusCode,
) : RuntimeException(status.message)