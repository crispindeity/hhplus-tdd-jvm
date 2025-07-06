package io.hhplus.tdd.util

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Log {
    fun <T : Any> getLogger(clazz: Class<T>): Logger = LoggerFactory.getLogger(clazz)

    fun <T> logging(
        log: Logger,
        function: (MutableMap<String, Any>) -> T?
    ): T {
        val logInfo: MutableMap<String, Any> = mutableMapOf()

        val startedAt: Long = now()
        logInfo["startedAt"] = startedAt

        val result: T? = function.invoke(logInfo)

        val endedAt: Long = now()
        logInfo["endedAt"] = endedAt

        logInfo["timeTaken"] = measureTime(startedAt, endedAt)

        log.info(logInfo.toString())
        return result ?: throw CustomException(ErrorCode.FAILED_TO_INVOKE_IN_LOG)
    }

    fun <T> warnLogging(
        log: Logger,
        function: (MutableMap<String, Any>) -> T?
    ): T {
        val logWarn: MutableMap<String, Any> = mutableMapOf()
        val result: T? = function.invoke(logWarn)

        log.warn(logWarn.toString())

        return result ?: throw CustomException(ErrorCode.FAILED_TO_INVOKE_IN_LOG)
    }

    fun <T> errorLogging(
        log: Logger,
        exception: Exception,
        function: (MutableMap<String, Any>) -> T?
    ): T {
        val logError: MutableMap<String, Any> = mutableMapOf()
        val result: T? = function.invoke(logError)

        log.error(logError.toString())
        log.error("stackTrace", exception)

        return result ?: throw CustomException(ErrorCode.FAILED_TO_INVOKE_IN_LOG)
    }

    private fun now() = System.currentTimeMillis()

    private fun measureTime(
        startedAt: Long,
        endedAt: Long
    ): Long = endedAt - startedAt
}
