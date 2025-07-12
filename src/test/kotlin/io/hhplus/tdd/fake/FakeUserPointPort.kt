package io.hhplus.tdd.fake

import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.point.port.output.UserPointCommandPort
import io.hhplus.tdd.point.port.output.UserPointQueryPort

class FakeUserPointPort :
    UserPointQueryPort,
    UserPointCommandPort {
    private val storage: MutableMap<Long, UserPoint> = mutableMapOf()

    fun singleUserPointFixture(
        id: Long,
        point: Long = 100,
        updateMillis: Long = 100
    ) {
        val userPoint =
            UserPoint(
                id = id,
                point = point,
                updateMillis = updateMillis
            )
        storage[userPoint.id] = userPoint
    }

    override fun findBy(id: Long): UserPoint =
        storage[id] ?: UserPoint(id = id, point = 0, updateMillis = System.currentTimeMillis())

    override fun chargeUserPoint(
        id: Long,
        amount: Long
    ): UserPoint {
        val userPoint =
            UserPoint(
                id = id,
                point = amount,
                updateMillis = System.currentTimeMillis()
            )
        storage[id] = userPoint
        return userPoint
    }

    override fun useUserPoint(
        id: Long,
        amount: Long
    ): UserPoint {
        val userPoint =
            UserPoint(
                id = id,
                point = amount,
                updateMillis = System.currentTimeMillis()
            )
        storage[id] = userPoint
        return userPoint
    }
}
