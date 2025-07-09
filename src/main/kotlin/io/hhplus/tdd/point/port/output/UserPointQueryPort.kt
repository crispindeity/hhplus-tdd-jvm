package io.hhplus.tdd.point.port.output

import io.hhplus.tdd.point.UserPoint

interface UserPointQueryPort {
    fun findBy(id: Long): UserPoint
}
