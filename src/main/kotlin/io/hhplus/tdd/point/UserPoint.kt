package io.hhplus.tdd.point

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long
) {
    companion object {
        private const val USER_POINT_LIMIT = 1_000_000
    }

    init {
        verifyMaxPointLimit(point)
    }

    fun pointCharge(amount: Long): UserPoint {
        verifyMaxPointLimit(amount)
        return this.copy(
            point = point + amount,
            updateMillis = System.currentTimeMillis()
        )
    }

    private fun verifyMaxPointLimit(amount: Long) {
        if (this.point + amount < USER_POINT_LIMIT) {
            return
        }
        throw CustomException(
            codeInterface = ErrorCode.EXCEEDS_MAX_POINT_LIMIT,
            additionalMessage = amount.toString()
        )
    }
}
