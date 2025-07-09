package io.hhplus.tdd.fake

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.port.output.PointHistoryCommandPort
import io.hhplus.tdd.point.port.output.PointHistoryQueryPort

class FakePointHistoryPort :
    PointHistoryQueryPort,
    PointHistoryCommandPort {
    private val storage: MutableMap<Long, PointHistory> = mutableMapOf()
    private var sequence: Long = 1L

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

    override fun save(pointHistory: PointHistory): PointHistory {
        val id: Long = pointHistory.id
        return if (storage.containsKey(id)) {
            storage[id] = pointHistory
            pointHistory
        } else {
            val newId: Long = sequence++
            val newPointHistory: PointHistory = pointHistory.copy(id = newId)
            storage[newId] = newPointHistory
            newPointHistory
        }
    }
}
