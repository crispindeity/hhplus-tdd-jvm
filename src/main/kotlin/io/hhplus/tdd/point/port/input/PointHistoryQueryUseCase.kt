package io.hhplus.tdd.point.port.input

interface PointHistoryQueryUseCase {
    data class PointHistoryResponses(
        val histories: List<PointHistoryResponse>,
        val count: Int
    )

    data class PointHistoryResponse(
        val id: Long,
        val userId: Long,
        val type: String,
        val amount: Long,
        val timeMillis: Long
    )

    fun retrievePointHistory(userId: Long): PointHistoryResponses
}
