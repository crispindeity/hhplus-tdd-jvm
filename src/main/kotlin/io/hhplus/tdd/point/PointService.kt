package io.hhplus.tdd.point

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode
import io.hhplus.tdd.point.port.input.UserPointQueryUseCase
import io.hhplus.tdd.point.port.output.UserPointQueryPort
import io.hhplus.tdd.util.Log
import org.slf4j.Logger

class PointService(
    private val userPointQueryPort: UserPointQueryPort
) : UserPointQueryUseCase {
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
}
