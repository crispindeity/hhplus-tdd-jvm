package io.hhplus.tdd.fake

import io.hhplus.tdd.point.port.input.UserPointQueryUseCase

class FakeUserPointQueryUseCase : UserPointQueryUseCase {
    override fun retrieveUserPoint(id: Long): UserPointQueryUseCase.UserPointResponse =
        UserPointQueryUseCase.UserPointResponse(
            id = id,
            point = 1000L,
            updateMillis = 100L
        )
}
