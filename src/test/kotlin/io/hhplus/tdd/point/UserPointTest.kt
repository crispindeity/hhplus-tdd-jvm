package io.hhplus.tdd.point

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UserPointTest {
    @Nested
    @DisplayName("유저 포인트 충전 기능 테스트")
    inner class PointChargeTest {
        @Nested
        @DisplayName("유저 포인트 충전 성공 테스트")
        inner class PointChargeSuccessTest {
            @Test
            @DisplayName("유저 포인트를 충전 할 수 있어야 한다.")
            fun userPointChargeTest() {
                // given
                val userPoint =
                    UserPoint(
                        id = 1L,
                        point = 1000L,
                        updateMillis = System.currentTimeMillis()
                    )

                val chargeAmount = 1000L

                // when
                val actual: UserPoint = userPoint.pointCharge(chargeAmount)

                // then
                Assertions.assertThat(actual.point).isEqualTo(userPoint.point + chargeAmount)
            }
        }

        @Nested
        @DisplayName("유저 포인트 충전 실패 테스트")
        inner class PointChargeFailTest {
            @ValueSource(longs = [0, -1])
            @ParameterizedTest(name = "요청 포인트: {0}")
            @DisplayName("포인트 충전 값이 0 또는 음수인 경우 예외가 발생해야 한다.")
            fun userPointChargeTest(amount: Long) {
                // given
                val userPoint =
                    UserPoint(
                        id = 1L,
                        point = 1000L,
                        updateMillis = System.currentTimeMillis()
                    )

                // when & then
                Assertions
                    .assertThatThrownBy { userPoint.pointCharge(amount) }
                    .isInstanceOf(CustomException::class.java)
                    .message()
                    .isEqualTo(
                        "${ErrorCode.POINTS_CHARGE_VERIFY_FAIL.message} - requestPoint: $amount, currentPoint: 1000"
                    )
            }

            @Test
            @DisplayName("포인트 충전 값이 단건 최대 값을 초과하는 경우 예외가 발생해야 한다.")
            fun userPointChargeTest2() {
                // given
                val userPoint =
                    UserPoint(
                        id = 1L,
                        point = 1000L,
                        updateMillis = System.currentTimeMillis()
                    )
                val chargeAmount = 100_001L

                // when & then
                Assertions
                    .assertThatThrownBy { userPoint.pointCharge(chargeAmount) }
                    .isInstanceOf(CustomException::class.java)
                    .message()
                    .isEqualTo(
                        "${ErrorCode.POINTS_CHARGE_VERIFY_FAIL.message} - requestPoint: $chargeAmount, currentPoint: 1000"
                    )
            }

            @Test
            @DisplayName("유저가 보유 할 수 있는 포인트의 최대 값을 넘기는 경우 예외가 발생해야 한다.")
            fun userPointChargeTest3() {
                // given
                val userPoint =
                    UserPoint(
                        id = 1L,
                        point = 1_000_000L,
                        updateMillis = System.currentTimeMillis()
                    )
                val chargeAmount = 1L

                // when & then
                Assertions
                    .assertThatThrownBy { userPoint.pointCharge(chargeAmount) }
                    .isInstanceOf(CustomException::class.java)
                    .message()
                    .isEqualTo(
                        "${ErrorCode.POINTS_CHARGE_VERIFY_FAIL.message} - requestPoint: $chargeAmount, currentPoint: 1000000"
                    )
            }
        }
    }

    @Nested
    @DisplayName("유저 포인트 사용 기능 테스트")
    inner class PointUseTest {
        @Nested
        @DisplayName("유저 포인트 사용 성공 테스트")
        inner class PointUseSuccessTest {
            @Test
            @DisplayName("유저 포인트를 사용 할 수 있어야 한다.")
            fun userPointUseTest() {
                // given
                val userPoint =
                    UserPoint(
                        id = 1L,
                        point = 1000L,
                        updateMillis = System.currentTimeMillis()
                    )

                val useAmount = 1000L

                // when
                val actual: UserPoint = userPoint.pointUse(useAmount)

                // then
                Assertions.assertThat(actual.point).isEqualTo(userPoint.point - useAmount)
            }
        }

        @Nested
        @DisplayName("유저 포인트 사용 실패 테스트")
        inner class PointUseFailTest {
            @Test
            @DisplayName("포인트 사용 값이 음수인 경우 예외가 발생해야 한다.")
            fun userPointUseTest() {
                // given
                val userPoint =
                    UserPoint(
                        id = 1L,
                        point = 1000L,
                        updateMillis = System.currentTimeMillis()
                    )

                val amount = -1L

                // when & then
                Assertions
                    .assertThatThrownBy { userPoint.pointUse(amount) }
                    .isInstanceOf(CustomException::class.java)
                    .message()
                    .isEqualTo(
                        "${ErrorCode.POINTS_USE_VERIFY_FAIL.message} - requestPoint: $amount, currentPoint: 1000"
                    )
            }

            @Test
            @DisplayName("유저가 보유한 포인트보다 많이 사용하는 경우 예외가 발생해야 한다.")
            fun userPointUseTest2() {
                // given
                val userPoint =
                    UserPoint(
                        id = 1L,
                        point = 1000L,
                        updateMillis = System.currentTimeMillis()
                    )

                val amount = 1001L

                // when & then
                Assertions
                    .assertThatThrownBy { userPoint.pointUse(amount) }
                    .isInstanceOf(CustomException::class.java)
                    .message()
                    .isEqualTo(
                        "${ErrorCode.POINTS_USE_VERIFY_FAIL.message} - requestPoint: $amount, currentPoint: 1000"
                    )
            }
        }
    }
}
