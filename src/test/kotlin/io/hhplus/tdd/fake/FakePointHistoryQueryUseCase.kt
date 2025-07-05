package io.hhplus.tdd.fake

import io.hhplus.tdd.point.port.input.PointHistoryQueryUseCase

class FakePointHistoryQueryUseCase : PointHistoryQueryUseCase {
    override fun retrievePointHistory(
        userId: Long
    ): PointHistoryQueryUseCase.PointHistoryResponses =
        PointHistoryQueryUseCase.PointHistoryResponses(
            responses =
                listOf(
                    PointHistoryQueryUseCase.PointHistoryResponse(
                        id = 1L,
                        userId = userId,
                        type = "CHARGE",
                        amount = 1000L,
                        timeMillis = 100L
                    ),
                    PointHistoryQueryUseCase.PointHistoryResponse(
                        id = 2L,
                        userId = userId,
                        type = "USE",
                        amount = 100L,
                        timeMillis = 101L
                    )
                ),
            count = 2
        )
}
