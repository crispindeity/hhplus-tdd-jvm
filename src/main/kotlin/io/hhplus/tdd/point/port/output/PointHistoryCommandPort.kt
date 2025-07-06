package io.hhplus.tdd.point.port.output

import io.hhplus.tdd.point.PointHistory

interface PointHistoryCommandPort {
    fun save(pointHistory: PointHistory): PointHistory
}
