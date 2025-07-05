package io.hhplus.tdd.exception

class CustomException(
    codeInterface: CodeInterface,
    additionalMessage: String? = null
) : RuntimeException(
        additionalMessage?.let { "${codeInterface.message} - $it" }
            ?: codeInterface.message
    )
