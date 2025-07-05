package io.hhplus.tdd

import io.hhplus.tdd.point.response.PointResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException

data class ErrorResponse(
    val code: String,
    val message: String
)

@ResponseStatus(HttpStatus.OK)
@RestControllerAdvice
class ApiControllerAdvice {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    data class ValidationErrors(
        val errors: List<ValidationError>
    )

    data class ValidationError(
        val field: String?,
        val value: Any?
    ) {
        companion object {
            fun of(
                field: String?,
                value: Any?
            ): ValidationError = ValidationError(field, value)
        }
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handlerMethodValidationException(
        exception: HandlerMethodValidationException
    ): PointResponse<ValidationErrors> {
        val errors: List<ValidationError> =
            exception.valueResults
                .map {
                    ValidationError.of(
                        field = it.methodParameter.parameterName,
                        value = it.argument
                    )
                }

        return PointResponse.fail(
            code = 400,
            message = "invalid request value",
            result = ValidationErrors(errors)
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): PointResponse<Unit> =
        PointResponse.fail(500, "internet server error")
}
