package com.planverse.server.common.aop

import com.planverse.server.common.util.ObjectUtil
import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Aspect
@Component
class TransactionAspect : CommonAspect() {
    @AfterReturning(value = "pointGetMapping()")
    fun afterPointGet(jp: JoinPoint) {
        val methodName = (jp.signature as MethodSignature).method.name
        val arg = ObjectUtil.convertObjectToString(jp.args)
    }

    @AfterReturning(value = "pointPostMapping()")
    fun afterPointPost(jp: JoinPoint) {
        val methodName = (jp.signature as MethodSignature).method.name
        val arg = ObjectUtil.convertObjectToString(jp.args)
    }
}