package io.hhplus.tdd.point.port.output

import io.hhplus.tdd.point.PointHistory

interface PointHistoryQueryPort {
    fun findBy(userId: Long): List<PointHistory>

    fun exists(userId: Long): Boolean
}
