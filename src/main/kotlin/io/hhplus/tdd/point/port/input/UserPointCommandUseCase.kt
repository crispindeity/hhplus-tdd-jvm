package io.hhplus.tdd.point.port.input

interface UserPointCommandUseCase {
    data class ChargeUserPointResponse(
        val id: Long,
        val point: Long,
        val updateMillis: Long
    )

    data class UseUserPointResponse(
        val id: Long,
        val point: Long,
        val updateMillis: Long
    )

    fun chargeUserPoint(
        id: Long,
        amount: Long
    ): ChargeUserPointResponse

    fun useUserPoint(
        id: Long,
        amount: Long
    ): UseUserPointResponse
}
