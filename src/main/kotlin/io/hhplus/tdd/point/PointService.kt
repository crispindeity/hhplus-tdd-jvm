package io.hhplus.tdd.point

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode
import io.hhplus.tdd.point.extensions.toDto
import io.hhplus.tdd.point.port.input.PointHistoryQueryUseCase
import io.hhplus.tdd.point.port.input.UserPointQueryUseCase
import io.hhplus.tdd.point.port.output.PointHistoryQueryPort
import io.hhplus.tdd.point.port.output.UserPointQueryPort
import io.hhplus.tdd.util.Log
import org.slf4j.Logger

class PointService(
    private val userPointQueryPort: UserPointQueryPort,
    private val pointHistoryQueryPort: PointHistoryQueryPort
) : UserPointQueryUseCase,
    PointHistoryQueryUseCase {
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
}
