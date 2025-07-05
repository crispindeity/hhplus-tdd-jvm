package io.hhplus.tdd.adapter

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.port.output.PointHistoryQueryPort
import org.springframework.stereotype.Component

@Component
class PointHistoryPersistenceAdapter(
    private val pointHistoryTable: PointHistoryTable
) : PointHistoryQueryPort {
    override fun findBy(userId: Long): List<PointHistory> =
        pointHistoryTable.selectAllByUserId(userId)

    override fun exists(userId: Long): Boolean =
        pointHistoryTable.selectAllByUserId(userId).isNotEmpty()
}
