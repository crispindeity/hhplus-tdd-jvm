package io.hhplus.tdd.fake

import io.hhplus.tdd.point.port.input.UserPointCommandUseCase

class FakeUserPointCommandUseCase : UserPointCommandUseCase {
    override fun chargeUserPoint(
        id: Long,
        amount: Long
    ): UserPointCommandUseCase.ChargeUserPointResponse =
        UserPointCommandUseCase.ChargeUserPointResponse(
            id = id,
            point = amount,
            updateMillis = 100L
        )

    override fun useUserPoint(
        id: Long,
        amount: Long
    ): UserPointCommandUseCase.UseUserPointResponse =
        UserPointCommandUseCase.UseUserPointResponse(
            id = 1L,
            point = 0,
            updateMillis = 101
        )
}
