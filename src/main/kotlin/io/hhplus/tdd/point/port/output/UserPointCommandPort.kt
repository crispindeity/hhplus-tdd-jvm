package io.hhplus.tdd.point.port.output

import io.hhplus.tdd.point.UserPoint

interface UserPointCommandPort {
    fun chargeUserPoint(
        id: Long,
        amount: Long
    ): UserPoint
}
