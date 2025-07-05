package io.hhplus.tdd.adapter

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.point.port.output.UserPointCommandPort
import io.hhplus.tdd.point.port.output.UserPointQueryPort
import org.springframework.stereotype.Component

@Component
class UserPointPersistenceAdapter(
    private val userPointTable: UserPointTable
) : UserPointQueryPort,
    UserPointCommandPort {
    override fun findBy(id: Long): UserPoint? = userPointTable.selectById(id)

    override fun chargeUserPoint(
        id: Long,
        amount: Long
    ): UserPoint =
        userPointTable.insertOrUpdate(
            id = id,
            amount = amount
        )

    override fun useUserPoint(
        id: Long,
        amount: Long
    ): UserPoint =
        userPointTable.insertOrUpdate(
            id = id,
            amount = amount
        )
}
