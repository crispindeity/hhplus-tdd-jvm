package io.hhplus.tdd.point.extensions

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.port.input.PointHistoryQueryUseCase

private const val CHARGE: String = "CHARGE"
private const val USE: String = "USE"

fun PointHistory.toDto(): PointHistoryQueryUseCase.PointHistoryResponse =
    PointHistoryQueryUseCase.PointHistoryResponse(
        id = this.id,
        userId = this.userId,
        type =
            when (this.type) {
                TransactionType.CHARGE -> CHARGE
                TransactionType.USE -> USE
            },
        amount = this.amount,
        timeMillis = this.timeMillis
    )
