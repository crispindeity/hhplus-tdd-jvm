package io.hhplus.tdd.point.response

import com.fasterxml.jackson.annotation.JsonInclude

class PointResponse<T> private constructor(
    private val code: Int,
    private val message: String,
    private val result: T? = null
) {
    companion object {
        fun <T> success(
            code: Int = 200,
            message: String = "success",
            result: T? = null
        ): PointResponse<T> = PointResponse(code = code, message = message, result = result)

        fun <T> fail(
            code: Int,
            message: String,
            result: T? = null
        ): PointResponse<T> = PointResponse(code = code, message = message, result = result)
    }

    fun getCode(): Int = code

    fun getMessage(): String = message

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    fun getResult(): T? = result
}
