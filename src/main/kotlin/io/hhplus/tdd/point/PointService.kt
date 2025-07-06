package io.hhplus.tdd.point

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode
import io.hhplus.tdd.point.extensions.toDto
import io.hhplus.tdd.point.port.input.PointHistoryQueryUseCase
import io.hhplus.tdd.point.port.input.UserPointCommandUseCase
import io.hhplus.tdd.point.port.input.UserPointQueryUseCase
import io.hhplus.tdd.point.port.output.PointHistoryCommandPort
import io.hhplus.tdd.point.port.output.PointHistoryQueryPort
import io.hhplus.tdd.point.port.output.UserPointCommandPort
import io.hhplus.tdd.point.port.output.UserPointQueryPort
import io.hhplus.tdd.util.Log
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class PointService(
    private val userPointQueryPort: UserPointQueryPort,
    private val pointHistoryQueryPort: PointHistoryQueryPort,
    private val userPointCommandPort: UserPointCommandPort,
    private val pointHistoryCommandPort: PointHistoryCommandPort
) : UserPointQueryUseCase,
    PointHistoryQueryUseCase,
    UserPointCommandUseCase {
    private val logger: Logger = Log.getLogger(PointService::class.java)

    override fun retrieveUserPoint(id: Long): UserPointQueryUseCase.UserPointResponse =
        Log.logging(logger) { log ->
            log["method"] = "retrieveUserPoint()"

            val foundUserPoint: UserPoint =
                userPointQueryPort.findBy(id) ?: throw CustomException(
                    codeInterface = ErrorCode.NOT_FOUND_USER_POINT,
                    additionalMessage = id.toString()
                )

            return@logging UserPointQueryUseCase.UserPointResponse(
                id = foundUserPoint.id,
                point = foundUserPoint.point,
                updateMillis = foundUserPoint.updateMillis
            )
        }

    override fun retrievePointHistory(
        userId: Long
    ): PointHistoryQueryUseCase.PointHistoryResponses =
        Log.logging(logger) { log ->
            log["method"] = "retrievePointHistory()"

            if (!pointHistoryQueryPort.exists(userId)) {
                throw CustomException(
                    codeInterface = ErrorCode.NOT_FOUND_POINT_HISTORY,
                    additionalMessage = userId.toString()
                )
            }

            val pointHistories: List<PointHistory> = pointHistoryQueryPort.findBy(userId)

            return@logging PointHistoryQueryUseCase.PointHistoryResponses(
                responses = pointHistories.map { it.toDto() },
                count = pointHistories.size
            )
        }

    override fun chargeUserPoint(
        id: Long,
        amount: Long
    ): UserPointCommandUseCase.ChargeUserPointResponse =
        Log.logging(logger) { log ->
            log["method"] = "chargeUserPoint()"
            val userPoint: UserPoint =
                userPointQueryPort
                    .findBy(id)
                    ?.pointCharge(amount)
                    ?: UserPoint(
                        id = id,
                        point = amount,
                        updateMillis = System.currentTimeMillis()
                    )

            val chargedUserPoint: UserPoint =
                userPointCommandPort.chargeUserPoint(
                    id = userPoint.id,
                    amount = userPoint.point
                )

            pointHistoryCommandPort.save(
                PointHistory(
                    userId = id,
                    type = TransactionType.CHARGE,
                    amount = amount,
                    timeMillis = System.currentTimeMillis()
                )
            )

            UserPointCommandUseCase.ChargeUserPointResponse(
                id = chargedUserPoint.id,
                point = chargedUserPoint.point,
                updateMillis = chargedUserPoint.updateMillis
            )
        }

    override fun useUserPoint(
        id: Long,
        amount: Long
    ) = Log.logging(logger) { log ->
        log["method"] = "useUserPoint()"

        val foundUserPoint: UserPoint =
            userPointQueryPort.findBy(id) ?: throw CustomException(
                codeInterface = ErrorCode.NOT_FOUND_USER_POINT,
                additionalMessage = id.toString()
            )

        val usedUserPoint: UserPoint = foundUserPoint.pointUse(amount)

        val updatedUserPoint: UserPoint =
            userPointCommandPort.useUserPoint(
                id = usedUserPoint.id,
                amount = usedUserPoint.point
            )

        pointHistoryCommandPort.save(
            PointHistory(
                userId = id,
                type = TransactionType.USE,
                amount = amount,
                timeMillis = System.currentTimeMillis()
            )
        )

        return@logging UserPointCommandUseCase.UseUserPointResponse(
            id = updatedUserPoint.id,
            point = updatedUserPoint.point,
            updateMillis = updatedUserPoint.updateMillis
        )
    }
}
