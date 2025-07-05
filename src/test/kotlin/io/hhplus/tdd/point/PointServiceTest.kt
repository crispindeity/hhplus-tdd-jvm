package io.hhplus.tdd.point

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode
import io.hhplus.tdd.fake.FakePointHistoryQueryPort
import io.hhplus.tdd.fake.FakeUserPointQueryPort
import io.hhplus.tdd.point.port.input.PointHistoryQueryUseCase
import io.hhplus.tdd.point.port.input.UserPointQueryUseCase
import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PointServiceTest {
    private lateinit var pointService: PointService
    private lateinit var fakeUserPointQueryPort: FakeUserPointQueryPort
    private lateinit var fakePointHistoryQueryPort: FakePointHistoryQueryPort

    @BeforeEach
    fun setUp() {
        fakeUserPointQueryPort = FakeUserPointQueryPort()
        fakePointHistoryQueryPort = FakePointHistoryQueryPort()
        pointService =
            PointService(
                userPointQueryPort = fakeUserPointQueryPort,
                pointHistoryQueryPort = fakePointHistoryQueryPort
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

                fakeUserPointQueryPort.singleUserPointFixture(userId)

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

                fakeUserPointQueryPort.singleUserPointFixture(2L)

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

                fakePointHistoryQueryPort.singlePointHistoryFixture(
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

                fakePointHistoryQueryPort.singlePointHistoryFixture(
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
}
