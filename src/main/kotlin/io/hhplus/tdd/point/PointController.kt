package io.hhplus.tdd.point

import io.hhplus.tdd.annotation.PositiveAmount
import io.hhplus.tdd.point.port.input.PointHistoryQueryUseCase
import io.hhplus.tdd.point.port.input.UserPointCommandUseCase
import io.hhplus.tdd.point.port.input.UserPointQueryUseCase
import io.hhplus.tdd.point.response.PointResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/point")
class PointController(
    private val userPointQueryUseCase: UserPointQueryUseCase,
    private val pointHistoryQueryUseCase: PointHistoryQueryUseCase,
    private val userPointCommandUseCase: UserPointCommandUseCase
) {
    @GetMapping("{id}")
    fun point(
        @PathVariable id: Long
    ): PointResponse<UserPointQueryUseCase.UserPointResponse> {
        val response: UserPointQueryUseCase.UserPointResponse =
            userPointQueryUseCase.retrieveUserPoint(id)

        return PointResponse.success(result = response)
    }

    @GetMapping("{id}/histories")
    fun history(
        @PathVariable id: Long
    ): PointResponse<PointHistoryQueryUseCase.PointHistoryResponses> {
        val response: PointHistoryQueryUseCase.PointHistoryResponses =
            pointHistoryQueryUseCase.retrievePointHistory(id)

        return PointResponse.success(result = response)
    }

    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody @PositiveAmount amount: Long
    ): PointResponse<UserPointCommandUseCase.ChargeUserPointResponse> {
        val response: UserPointCommandUseCase.ChargeUserPointResponse =
            userPointCommandUseCase.chargeUserPoint(
                id = id,
                amount = amount
            )

        return PointResponse.success(result = response)
    }

    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestBody @PositiveAmount amount: Long
    ): PointResponse<UserPointCommandUseCase.UseUserPointResponse> {
        val response: UserPointCommandUseCase.UseUserPointResponse =
            userPointCommandUseCase.useUserPoint(
                id = id,
                amount = amount
            )

        return PointResponse.success(result = response)
    }
}
