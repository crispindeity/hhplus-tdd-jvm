package io.hhplus.tdd.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Log {
    private val objectMapper = jacksonObjectMapper()

    fun <T : Any> getLogger(clazz: Class<T>): Logger = LoggerFactory.getLogger(clazz)

    fun <T> logging(
        log: Logger,
        function: (MutableMap<String, Any>) -> T?
    ): T {
        val logInfo: MutableMap<String, Any> = mutableMapOf()
        val startedAt: Long = now()
        logInfo["startedAt"] = startedAt

        val result: Result<T?> = runCatching { function(logInfo) }
        val endedAt: Long = now()

        logInfo["endedAt"] = endedAt
        logInfo["timeTaken"] = endedAt - startedAt

        result.fold(
            onSuccess = {
                log.info(toJson(logInfo))
                return it ?: throw CustomException(ErrorCode.FAILED_TO_INVOKE_IN_LOG)
            },
            onFailure = {
                logInfo["exceptionMessage"] = it.message ?: "Unknown"
                logInfo["exceptionType"] = it::class.simpleName.orEmpty()
                log.info(toJson(logInfo))
                throw it
            }
        )
    }

    fun <T> warnLogging(
        log: Logger,
        function: (MutableMap<String, Any>) -> T?
    ): T {
        val logWarn: MutableMap<String, Any> = mutableMapOf()
        val result: T? = function(logWarn)
        log.warn(toJson(logWarn))
        return result ?: throw CustomException(ErrorCode.FAILED_TO_INVOKE_IN_LOG)
    }

    fun <T> errorLogging(
        log: Logger,
        exception: Exception,
        function: (MutableMap<String, Any>) -> T?
    ): T {
        val logError: MutableMap<String, Any> = mutableMapOf()
        val result: T? = function(logError)

        logError["exceptionMessage"] = exception.message ?: "Unknown"
        logError["exceptionType"] = exception::class.simpleName.orEmpty()

        log.error(toJson(logError))
        log.error("trace", exception)
        return result ?: throw CustomException(ErrorCode.FAILED_TO_INVOKE_IN_LOG)
    }

    private fun now(): Long = System.currentTimeMillis()

    private fun toJson(map: Map<String, Any>): String =
        try {
            objectMapper.writeValueAsString(map)
        } catch (_: Exception) {
            map.toString()
        }
}
