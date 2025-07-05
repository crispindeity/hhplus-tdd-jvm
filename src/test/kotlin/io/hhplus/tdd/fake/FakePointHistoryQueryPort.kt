package io.hhplus.tdd.fake

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.port.output.PointHistoryQueryPort

class FakePointHistoryQueryPort : PointHistoryQueryPort {
    private val storage: MutableMap<Long, PointHistory> = mutableMapOf()

    fun singlePointHistoryFixture(
        id: Long,
        userId: Long,
        type: TransactionType = TransactionType.CHARGE,
        amount: Long = 1000,
        timeMillis: Long = 100
    ) {
        storage[id] =
            PointHistory(
                id = id,
                userId = userId,
                type = type,
                amount = amount,
                timeMillis = timeMillis
            )
    }

    override fun findBy(userId: Long): List<PointHistory> =
        storage.values.filter { it.userId == userId }

    override fun exists(userId: Long): Boolean = storage.values.any { it.userId == userId }
}
