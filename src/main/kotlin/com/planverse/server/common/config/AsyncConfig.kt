package com.planverse.server.common.config

import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@EnableAsync
@Configuration
class AsyncConfig : AsyncConfigurer {
    override fun getAsyncExecutor(): Executor {
        val threadPoolTaskExecutor = ThreadPoolTaskExecutor()
        threadPoolTaskExecutor.corePoolSize = 1
        threadPoolTaskExecutor.maxPoolSize = 5
        threadPoolTaskExecutor.queueCapacity = 10
        threadPoolTaskExecutor.setTaskDecorator(LoggingTaskDecorator())
        threadPoolTaskExecutor.setThreadNamePrefix("Async-ThreadPool-Executor-")
        threadPoolTaskExecutor.initialize()

        return threadPoolTaskExecutor
    }
}

class LoggingTaskDecorator : TaskDecorator {
    override fun decorate(task: Runnable): Runnable {
        val callerThreadContext = MDC.getCopyOfContextMap()
        return Runnable {
            callerThreadContext?.let {
                MDC.setContextMap(it)
            }
            task.run()
        }
    }
}
