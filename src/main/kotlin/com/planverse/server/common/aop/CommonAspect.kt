package com.planverse.server.common.aop

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class CommonAspect {
    @Pointcut(value = "execution(* com.planverse.server.*.controller..*Controller.*(..))")
    fun pointAllController() {
    }

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.GetMapping)")
    fun pointGetMapping() {
    }

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    fun pointPostMapping() {
    }

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.PutMapping)")
    fun pointPutMapping() {
    }
}