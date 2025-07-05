package io.hhplus.tdd.exception

interface CodeInterface {
    val code: Int
    val message: String
}

enum class ErrorCode(
    override val code: Int,
    override val message: String
) : CodeInterface {
    FAILED_TO_INVOKE_IN_LOG(code = 100, message = "failed to invoke in log."),
    NOT_FOUND_USER_POINT(code = 404, message = "not found user point")
}
