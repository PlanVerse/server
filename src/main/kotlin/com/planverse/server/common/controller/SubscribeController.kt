package com.planverse.server.common.controller

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface SubscribeController {
    fun subscribe(userId: Long): SseEmitter
}