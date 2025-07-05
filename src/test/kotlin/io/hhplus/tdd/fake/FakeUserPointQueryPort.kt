package io.hhplus.tdd.fake

import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.point.port.output.UserPointQueryPort

class FakeUserPointQueryPort : UserPointQueryPort {
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

    override fun findBy(id: Long): UserPoint? = storage[id]
}
