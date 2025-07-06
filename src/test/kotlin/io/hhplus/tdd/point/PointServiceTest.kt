package io.hhplus.tdd.point

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode
import io.hhplus.tdd.fake.FakePointHistoryPort
import io.hhplus.tdd.fake.FakeUserPointPort
import io.hhplus.tdd.point.port.input.PointHistoryQueryUseCase
import io.hhplus.tdd.point.port.input.UserPointCommandUseCase
import io.hhplus.tdd.point.port.input.UserPointQueryUseCase
import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PointServiceTest {
    private lateinit var pointService: PointService
    private lateinit var fakeUserPointPort: FakeUserPointPort
    private lateinit var fakePointHistoryPort: FakePointHistoryPort

    @BeforeEach
    fun setUp() {
        fakeUserPointPort = FakeUserPointPort()
        fakePointHistoryPort = FakePointHistoryPort()
        pointService =
            PointService(
                userPointQueryPort = fakeUserPointPort,
                pointHistoryQueryPort = fakePointHistoryPort,
                userPointCommandPort = fakeUserPointPort,
                pointHistoryCommandPort = fakePointHistoryPort
            )
    }

    @Nested
    @DisplayName("유저 포인트 조회 조회 테스트")
    inner class RetrieveUserPointTest {
        @Nested
        @DisplayName("유저 포인트 조회 성공 테스트")
        inner class RetrieveUserPointSuccessTest {
            @Test
            @DisplayName("유저 아이디를 통해 해당 유저의 포인트를 조회할 수 있어야 한다.")
            fun retrieveUserPointTest() {
                // given
                val userId = 1L

                fakeUserPointPort.singleUserPointFixture(userId)

                // when
                val actual: UserPointQueryUseCase.UserPointResponse =
                    pointService.retrieveUserPoint(userId)

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions.assertThat(actual).isNotNull
                    softAssertions.assertThat(actual.id).isEqualTo(userId)
                }
            }
        }

        @Nested
        @DisplayName("유저 포인트 조회 실패 테스트")
        inner class RetrieveUserPointFailTest {
            @Test
            @DisplayName("존재하지 않는 유저 아이디로 포인트 조회 시 예외가 발생해야 한다.")
            fun retrieveUserPointTest() {
                // given
                val wrongUserId = 1L

                fakeUserPointPort.singleUserPointFixture(2L)

                // when & then
                Assertions
                    .assertThatThrownBy {
                        pointService.retrieveUserPoint(wrongUserId)
                    }.isInstanceOf(CustomException::class.java)
                    .hasMessage("${ErrorCode.NOT_FOUND_USER_POINT.message} - $wrongUserId")
            }
        }
    }

    @Nested
    @DisplayName("포인트 히스토리 조회 테스트")
    inner class RetrievePointHistoryTest {
        @Nested
        @DisplayName("포인트 히스토리 조회 성공 테스트")
        inner class RetrievePointHistorySuccessTest {
            @Test
            @DisplayName("유저 아이디를 통해 해당 유저의 포인트 히스토리를 조회할 수 있어야 한다.")
            fun retrievePointHistoryTest() {
                // given
                val userId = 1L

                fakePointHistoryPort.singlePointHistoryFixture(
                    id = 1L,
                    userId = userId
                )

                // when
                val actual: PointHistoryQueryUseCase.PointHistoryResponses =
                    pointService.retrievePointHistory(userId)

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions.assertThat(actual).isNotNull
                    softAssertions.assertThat(actual.responses.first().userId).isEqualTo(userId)
                }
            }
        }

        @Nested
        @DisplayName("포인트 히스토리 조회 실패 테스트")
        inner class RetrievePointHistoryFailTest {
            @Test
            @DisplayName("존재하지 않는 유저 아이디로 포인트 히스토리 조회 시 예외가 발생해야 한다.")
            fun retrievePointHistoryTest() {
                // given
                val wrongUserId = 1L

                fakePointHistoryPort.singlePointHistoryFixture(
                    id = 1L,
                    userId = 2L
                )

                // when & then
                Assertions
                    .assertThatThrownBy {
                        pointService.retrievePointHistory(wrongUserId)
                    }.isInstanceOf(CustomException::class.java)
                    .hasMessage("${ErrorCode.NOT_FOUND_POINT_HISTORY.message} - $wrongUserId")
            }
        }
    }

    @Nested
    @DisplayName("유저 포인트 충전 테스트 ")
    inner class ChargeUserPointTest {
        @Nested
        @DisplayName("유저 포인트 충전 성공 테스트")
        inner class ChargeUserPointSuccessTest {
            @Test
            @DisplayName("신규 유저에 대해 포인트를 충전할 수 있어야 한다.")
            fun chargeUserPointTest() {
                // give
                val userId = 1L
                val amount = 1000L

                // when
                val actual: UserPointCommandUseCase.ChargeUserPointResponse =
                    pointService.chargeUserPoint(
                        id = userId,
                        amount = amount
                    )

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions.assertThat(actual).isNotNull
                    softAssertions.assertThat(actual.point).isEqualTo(1000)
                    softAssertions
                        .assertThat(fakePointHistoryPort.findBy(userId).first().type)
                        .isEqualTo(
                            TransactionType.CHARGE
                        )
                }
            }

            @Test
            @DisplayName("기존 유저에 대해 포인트를 충전 할 수 있어야 한다.")
            fun chargeUserPointTest2() {
                // given
                val userId = 1L
                val amount = 1000L

                fakeUserPointPort.singleUserPointFixture(
                    id = userId,
                    point = 1000L
                )

                // when
                val actual: UserPointCommandUseCase.ChargeUserPointResponse =
                    pointService.chargeUserPoint(
                        id = userId,
                        amount = amount
                    )

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions.assertThat(actual).isNotNull
                    softAssertions.assertThat(actual.point).isEqualTo(2000)
                }
            }
        }

        @Nested
        @DisplayName("유저 포인트 충전 실패 테스트")
        inner class ChargeUserPointFailTest {
            @Test
            @DisplayName("포인트 충전 시 신규 유저가 보유한 포인트가 limit(1_000_000) 을 초과하면 예외가 발생해야 한다.")
            fun chargeUserPointTest() {
                // given
                val userId = 1L
                val amount = 1_000_000L

                // when & then
                Assertions
                    .assertThatThrownBy {
                        pointService.chargeUserPoint(
                            id = userId,
                            amount = amount
                        )
                    }.isInstanceOf(CustomException::class.java)
                    .hasMessage("${ErrorCode.EXCEEDS_MAX_POINT_LIMIT.message} - $amount")
            }

            @Test
            @DisplayName("포인트 충전 시 기존 유저가 보유한 포인트가 limit(1_000_000) 을 초과하면 예외가 발생해야 한다.")
            fun chargeUserPointTest2() {
                // given
                val userId = 1L
                val amount = 999_999L

                fakeUserPointPort.singleUserPointFixture(
                    id = userId,
                    point = 1L
                )

                // when & then
                Assertions
                    .assertThatThrownBy {
                        pointService.chargeUserPoint(
                            id = userId,
                            amount = amount
                        )
                    }.isInstanceOf(CustomException::class.java)
                    .hasMessage("${ErrorCode.EXCEEDS_MAX_POINT_LIMIT.message} - $amount")
            }
        }
    }

    @Nested
    @DisplayName("유저 포인트 사용 테스트")
    inner class UseUserPointTest {
        @Nested
        @DisplayName("유저 포인트 사용 성공 테스트")
        inner class UseUserPointSuccessTest {
            @Test
            @DisplayName("유저는 보유한 포인트를 사용할 수 있어야 한다.")
            fun useUserPointTest() {
                // given
                val userId = 1L
                val amount = 1000L

                fakeUserPointPort.singleUserPointFixture(
                    id = userId,
                    point = amount
                )

                // when
                val actual: UserPointCommandUseCase.UseUserPointResponse =
                    pointService.useUserPoint(
                        id = userId,
                        amount = amount
                    )

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions.assertThat(actual).isNotNull
                    softAssertions.assertThat(actual.point).isZero
                    softAssertions
                        .assertThat(fakePointHistoryPort.findBy(userId).first().type)
                        .isEqualTo(
                            TransactionType.USE
                        )
                }
            }
        }

        @Nested
        @DisplayName("유저 포인트 사용 실패 테스트")
        inner class UseUserPointFailTest {
            @Test
            @DisplayName("유저가 보유한 포인트보다 많이 사용할 경우 예외가 발생해야 한다.")
            fun useUserPointTest() {
                // given
                val userId = 1L
                val amount = 1000L

                fakeUserPointPort.singleUserPointFixture(
                    id = userId,
                    point = 100L
                )

                // when & then
                Assertions
                    .assertThatThrownBy {
                        pointService.useUserPoint(
                            id = userId,
                            amount = amount
                        )
                    }.isInstanceOf(CustomException::class.java)
                    .hasMessage(
                        "${ErrorCode.INSUFFICIENT_POINT.message} - requestPoint: $amount, currentPoint: 100"
                    )
            }
        }
    }
}
