package io.hhplus.tdd.adapter

import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.point.port.output.UserPointCommandPort
import io.hhplus.tdd.point.port.output.UserPointQueryPort
import org.springframework.stereotype.Component

@Component
class UserPointPersistenceAdapter :
    UserPointQueryPort,
    UserPointCommandPort {
    override fun findBy(id: Long): UserPoint? {
        TODO("Not yet implemented")
    }

    override fun chargeUserPoint(
        id: Long,
        amount: Long
    ): UserPoint {
        TODO("Not yet implemented")
    }
}
