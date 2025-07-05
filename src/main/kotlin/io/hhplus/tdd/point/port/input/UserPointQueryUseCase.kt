package io.hhplus.tdd.point.port.input

interface UserPointQueryUseCase {
    data class UserPointResponse(
        val id: Long,
        val point: Long,
        val updateMillis: Long
    )

    fun retrieveUserPoint(id: Long): UserPointResponse
}
