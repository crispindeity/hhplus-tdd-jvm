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
        private const val SINGLE_CHARGE_LIMIT = 100_000
    }

    fun pointCharge(amount: Long): UserPoint {
        verifyPointsCharge(amount)
        return this.copy(
            point = point + amount,
            updateMillis = System.currentTimeMillis()
        )
    }

    fun pointUse(amount: Long): UserPoint {
        verifyPointUse(amount)
        return this.copy(
            point = point - amount,
            updateMillis = System.currentTimeMillis()
        )
    }

    private fun verifyPointsCharge(amount: Long) {
        if (amount > 0 &&
            amount <= SINGLE_CHARGE_LIMIT &&
            this.point + amount <= USER_POINT_LIMIT
        ) {
            return
        }
        throw CustomException(
            codeInterface = ErrorCode.POINTS_CHARGE_VERIFY_FAIL,
            additionalMessage = "requestPoint: $amount, currentPoint: ${this.point}"
        )
    }

    private fun verifyPointUse(amount: Long) {
        if (amount >= 0 && this.point - amount >= 0) {
            return
        }
        throw CustomException(
            codeInterface = ErrorCode.POINTS_USE_VERIFY_FAIL,
            additionalMessage = "requestPoint: $amount, currentPoint: ${this.point}"
        )
    }
}
