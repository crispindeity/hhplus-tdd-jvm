package io.hhplus.tdd.kotest

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.PointService
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.point.port.input.PointHistoryQueryUseCase
import io.hhplus.tdd.point.port.input.UserPointCommandUseCase
import io.hhplus.tdd.point.port.input.UserPointQueryUseCase
import io.hhplus.tdd.point.port.output.PointHistoryCommandPort
import io.hhplus.tdd.point.port.output.PointHistoryQueryPort
import io.hhplus.tdd.point.port.output.UserPointCommandPort
import io.hhplus.tdd.point.port.output.UserPointQueryPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class PointServiceTest : DescribeSpec() {
    override fun isolationMode() = IsolationMode.InstancePerLeaf

    val pointHistoryQueryPort: PointHistoryQueryPort = mockk<PointHistoryQueryPort>()
    val pointHistoryCommandPort: PointHistoryCommandPort = mockk<PointHistoryCommandPort>()
    val userPointCommandPort: UserPointCommandPort = mockk<UserPointCommandPort>()
    val userPointQueryPort: UserPointQueryPort = mockk<UserPointQueryPort>()

    val pointService =
        PointService(
            userPointQueryPort = userPointQueryPort,
            userPointCommandPort = userPointCommandPort,
            pointHistoryQueryPort = pointHistoryQueryPort,
            pointHistoryCommandPort = pointHistoryCommandPort
        )

    init {
        describe("유저 포인트 조회 테스트") {
            val userId = 1L
            val point = 1000L
            val now: Long = System.currentTimeMillis()
            val userPoint =
                UserPoint(
                    id = userId,
                    point = point,
                    updateMillis = now
                )
            context("유저 포인트 조회 성공 테스트") {
                it("유저의 현재 포인트를 조회할 수 있어야 한다.") {
                    every { userPointQueryPort.findBy(userId) } returns userPoint

                    val actual: UserPointQueryUseCase.UserPointResponse =
                        pointService.retrieveUserPoint(userId)

                    actual.point shouldBe point

                    verify(exactly = 1) { userPointQueryPort.findBy(userId) }
                }
            }
        }

        describe("포인트 히스토리 조회 테스트") {
            val userId = 1L
            val now: Long = System.currentTimeMillis()
            val testHistory =
                PointHistory(
                    id = 1L,
                    userId = userId,
                    type = TransactionType.CHARGE,
                    amount = 1000L,
                    timeMillis = now
                )
            context("포인트 히스토리 조회 성공 테스트") {
                it("유저의 포인트 사용/충전 내역을 조회할 수 있어야 한다.") {
                    every { pointHistoryQueryPort.exists(userId) } returns true
                    every { pointHistoryQueryPort.findBy(userId) } returns listOf(testHistory)

                    val actual: PointHistoryQueryUseCase.PointHistoryResponses =
                        pointService.retrievePointHistory(userId)

                    actual.histories.first().type shouldBe TransactionType.CHARGE.name

                    verify(exactly = 1) { pointHistoryQueryPort.exists(userId) }
                    verify(exactly = 1) { pointHistoryQueryPort.findBy(userId) }
                }
            }
            context("포인트 히스토리 조회 실패 테스트") {
                it("존재 하지 않는 유저 아이디로 포인트 내역 조회 시 예외가 발생해야 한다.") {
                    every { pointHistoryQueryPort.exists(userId) } returns false

                    val actual: CustomException =
                        shouldThrow<CustomException> { pointService.retrievePointHistory(userId) }

                    actual.message shouldBe "not found user point history - 1"

                    verify(exactly = 1) { pointHistoryQueryPort.exists(userId) }
                }
            }
        }

        describe("유저 포인트 충전 테스트") {
            val userId = 1L
            val amount = 1000L
            val now: Long = System.currentTimeMillis()
            context("유저 포인트 충전 성공 테스트") {
                it("유저 포인트를 충전 할 수 있어야 한다.") {
                    val foundUserPoint =
                        UserPoint(
                            id = userId,
                            point = 0L,
                            updateMillis = now
                        )
                    val chargedUserPoint =
                        UserPoint(
                            id = userId,
                            point = foundUserPoint.point + amount,
                            updateMillis = now
                        )

                    every { userPointQueryPort.findBy(userId) } returns foundUserPoint
                    every {
                        userPointCommandPort.chargeUserPoint(
                            userId,
                            amount
                        )
                    } returns chargedUserPoint
                    every {
                        pointHistoryCommandPort.save(
                            match {
                                it.userId == userId &&
                                    it.amount == amount &&
                                    it.type == TransactionType.CHARGE
                            }
                        )
                    } returns
                        PointHistory(
                            userId = userId,
                            type = TransactionType.CHARGE,
                            amount = amount,
                            timeMillis = now
                        )

                    val actual: UserPointCommandUseCase.ChargeUserPointResponse =
                        pointService.chargeUserPoint(userId, amount)

                    actual.point shouldBe amount

                    verify(exactly = 1) { userPointQueryPort.findBy(userId) }
                    verify(exactly = 1) { userPointCommandPort.chargeUserPoint(userId, amount) }
                    verify(exactly = 1) {
                        pointHistoryCommandPort.save(
                            match {
                                it.userId == userId &&
                                    it.amount == amount &&
                                    it.type == TransactionType.CHARGE
                            }
                        )
                    }
                }
            }
            context("유저 포인트 충전 실패 테스트") {
                it("유저가 보유할 수 있는 포인트 최댓값(1_000_000)을 초과하여 충전하는 경우 예외가 발생해야 한다.") {
                    val foundUserPoint =
                        UserPoint(
                            id = userId,
                            point = 999_001L,
                            updateMillis = now
                        )

                    every { userPointQueryPort.findBy(userId) } returns foundUserPoint

                    val actual: CustomException =
                        shouldThrow<CustomException> {
                            pointService.chargeUserPoint(
                                userId,
                                amount
                            )
                        }

                    actual.message shouldBe "user point exceeds allowed maximum - 1000001"

                    verify(exactly = 1) { userPointQueryPort.findBy(userId) }
                }
            }
        }

        describe("유저 포인트 사용 테스트") {
            val userId = 1L
            val amount = 1000L
            val now: Long = System.currentTimeMillis()
            context("유저 포인트 사용 성공 테스트") {
                it("유저가 보유한 포인트를 사용할 수 있어야 한다.") {
                    val foundUserPoint =
                        UserPoint(
                            id = userId,
                            point = 999_001L,
                            updateMillis = now
                        )
                    val usedUserPoint =
                        UserPoint(
                            id = userId,
                            point = foundUserPoint.point - amount,
                            updateMillis = now
                        )

                    every { userPointQueryPort.findBy(userId) } returns foundUserPoint
                    every {
                        userPointCommandPort.useUserPoint(
                            id = userId,
                            amount = usedUserPoint.point
                        )
                    } returns usedUserPoint
                    every {
                        pointHistoryCommandPort.save(
                            match {
                                it.userId == userId &&
                                    it.amount == amount &&
                                    it.type == TransactionType.USE
                            }
                        )
                    } returns
                        PointHistory(
                            userId = userId,
                            type = TransactionType.USE,
                            amount = amount,
                            timeMillis = now
                        )

                    val actual: UserPointCommandUseCase.UseUserPointResponse =
                        pointService.useUserPoint(
                            id = userId,
                            amount = amount
                        )

                    actual.point shouldBe foundUserPoint.point - amount

                    verify(exactly = 1) { userPointQueryPort.findBy(userId) }
                    verify(exactly = 1) {
                        userPointCommandPort.useUserPoint(
                            id = userId,
                            amount = usedUserPoint.point
                        )
                    }
                    verify(exactly = 1) {
                        pointHistoryCommandPort.save(
                            match {
                                it.userId == userId &&
                                    it.amount == amount &&
                                    it.type == TransactionType.USE
                            }
                        )
                    }
                }
            }
            context("유저 포인트 사용 실패 테스트") {
                it("유저가 보유한 포인트보다 많이 사용하는 경우 예외가 발생해야 한다.") {
                    val foundUserPoint =
                        UserPoint(
                            id = userId,
                            point = 999L,
                            updateMillis = now
                        )
                    every { userPointQueryPort.findBy(userId) } returns foundUserPoint

                    val actual: CustomException =
                        shouldThrow<CustomException> {
                            pointService.useUserPoint(
                                id = userId,
                                amount = amount
                            )
                        }

                    actual.message shouldBe
                        "not enough point to complete the operation - requestPoint: 1000, currentPoint: 999"

                    verify(exactly = 1) { userPointQueryPort.findBy(userId) }
                }
            }
        }
    }
}
