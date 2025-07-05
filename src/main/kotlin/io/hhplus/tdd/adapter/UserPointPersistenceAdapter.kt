package io.hhplus.tdd.adapter

import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.point.port.output.UserPointQueryPort
import org.springframework.stereotype.Component

@Component
class UserPointPersistenceAdapter : UserPointQueryPort {
    override fun findBy(id: Long): UserPoint? {
        TODO("Not yet implemented")
    }
}
