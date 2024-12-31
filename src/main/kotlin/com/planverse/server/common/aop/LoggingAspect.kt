package com.planverse.server.common.aop

import com.planverse.server.common.util.ObjectUtil
import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Order(1)
@Aspect
@Component
class LoggingAspect {
    @Around(
        ("(execution(* com.planverse.server.*.controller..*Controller.*(..))"
                + " || execution(* com.planverse.server.*.service..*Service.*(..))"
                + " || execution(* com.planverse.server.*.mapper..*Mapper.*(..))"
                + " || execution(* com.planverse.server.*.repository..*Repository.*(..)))"
                + " && !@annotation(com.planverse.server.common.annotation.Except)"
                )
    )
    @Throws(Throwable::class)
    fun logging(pjp: ProceedingJoinPoint): Any? {
        val type: String
        val classNm = pjp.signature.declaringTypeName
        val methodNm = pjp.signature.name

        type = if (classNm.contains("Controller")) {
            "Controller"
        } else if (classNm.contains("Service")) {
            "Service"
        } else if (classNm.contains("Repository")) {
            "Repository"
        } else if (classNm.contains("Mapper")) {
            "Mapper"
        } else {
            "Etc"
        }

        val result = pjp.proceed()
        val res = if (result is ResponseEntity<*>) {
            result.body
        } else {
            result
        }

        logger.trace(" $type : '$classNm.$methodNm()', Data=${ObjectUtil.convertObjectToString(res)}")
        return result
    }
}
